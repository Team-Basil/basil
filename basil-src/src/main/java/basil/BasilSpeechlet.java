package basil;

import java.util.*;
import java.util.concurrent.*;

import org.apache.commons.lang3.*;
import org.slf4j.*;
import org.slf4j.Logger;

import com.amazon.speech.slu.*;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.*;
import com.firebase.client.*;

public class BasilSpeechlet implements Speechlet {
	private static final Logger log = LoggerFactory.getLogger(BasilSpeechlet.class);
	
	protected static String userId = "test-user";
	
	protected Firebase firebase;
	
	public BasilSpeechlet() {
		System.out.println("Constructing firebase...");
		firebase = new Firebase("https://projectbasil.firebaseio.com/" + userId);
	}
	
	@Override
	public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
	throws SpeechletException
	{
		log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
		return getWelcomeResponse();
	}
	
    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
    throws SpeechletException
    {
    	System.out.println("Intent called 2");
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        final Semaphore semaphore = new Semaphore(0);
        
        if ("NextLineIntent".equals(intentName))
        {
        	Recipe recipe = new Recipe();
        	readRecipe(recipe, semaphore);
        	
        	recipe.updateCurrentStep(1);
        	
        	updateFirebase("next-step", recipe, semaphore);
        	
            return say("Next, step " + recipe.getCurrentStepNumber() + ". " + recipe.getCurrentStep());
        }
        else if("PreviousLineIntent".equals(intentName))
        {
        	Recipe recipe = new Recipe();
        	readRecipe(recipe, semaphore);
        	
        	recipe.updateCurrentStep(-1);
        	
        	updateFirebase("previous-step", recipe, semaphore);
        	
        	return say("Going back a step, step " + recipe.getCurrentStepNumber() + ". " + recipe.getCurrentStep());
        }
        else if("AnyLineIntent".equals(intentName))
        {
        	Slot lineNumSlot = intent.getSlot("lineNum");
        	String lineNumOrdinal = lineNumSlot.getValue();
        	int lineNum = LineNumLookup.getLineNum(lineNumOrdinal);

        	Recipe recipe = new Recipe();
        	readRecipe(recipe, semaphore);
        	
        	recipe.setCurrentStep(lineNum);
        	
        	updateFirebase("goto-step", recipe, semaphore);
        	
        	return say("Moving to step " + recipe.getCurrentStepNumber() + ". " + recipe.getCurrentStep());
        }
        else if("LastLineIntent".equals(intentName))
        {
        	Recipe recipe = new Recipe();
        	readRecipe(recipe, semaphore);
        	
        	recipe.setCurrentStep(recipe.getNumberOfSteps());
        	
        	updateFirebase("last-step", recipe, semaphore);
        	
        	return say("Moving to the last step, step " + recipe.getCurrentStepNumber() + ". " + recipe.getCurrentStep());
        }
        else if("StartIntent".equals(intentName))
        {
        	Recipe recipe = new Recipe();
        	readRecipe(recipe, semaphore);
        	recipe.setCurrentStep(1);
        	
        	updateFirebase("start-recipe", recipe, semaphore);
        	
        	return say("Let's get started. Step 1. " + recipe.getCurrentStep());
        }
        else {
            throw new SpeechletException("Invalid Intent");
        }
    }
    
    protected void updateFirebase(String commandName, Recipe recipe, final Semaphore semaphore) {
    	
    	Map<String, Object> commandStructure = new HashMap();
    	commandStructure.put("commandName", commandName);
    	commandStructure.put("currentStep", recipe.getCurrentStepNumber());
    	
    	firebase.child("command").setValue(commandStructure, new Firebase.CompletionListener() {
			
			@Override
			public void onComplete(FirebaseError arg0, Firebase arg1) {
				System.out.println("Firebase completed.");
				semaphore.release();
			}
		});
    	
    	try {
    		System.out.println("About to acquire semaphore...");
    		semaphore.acquire();
    		System.out.println("Semaphore released...");
    	} catch(InterruptedException e) {
    		System.out.println("Semaphore interrupted...");
    	}
    }
    
    protected void readRecipe(final Recipe recipe, final Semaphore semaphore) {
    	firebase.addValueEventListener(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				System.out.println("Recipe: " + dataSnapshot.getValue());
				Map<String, Object> fullMap = (Map) dataSnapshot.getValue();
				
				Map<String, String> steps = (Map) fullMap.get("recipe");
				SortedMap<String, String> sortedSteps = new TreeMap(steps);
				
				List<String> stepList = new ArrayList(sortedSteps.values());
				
				Map<String, Object> commandStructure = (Map) fullMap.get("command");
				long currentStep = (Long) commandStructure.get("currentStep");
				
				recipe.set((int) currentStep, stepList);
				
				semaphore.release();
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {
			}
		});
    	
    	try {
    		System.out.println("About to acquire semaphore...");
    		semaphore.acquire();
    		System.out.println("Semaphore released...");
    	} catch(InterruptedException e) {
    		System.out.println("Semaphore interrupted...");
    	}
    }
    
    ///
    
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
    		throws SpeechletException
    {
    	log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
    			session.getSessionId());
    	// any initialization logic goes here
    }

    /**
     * Creates a {@code SpeechletResponse} for the hello intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    protected SpeechletResponse say(String speechText) {

    	// Create the Simple card content.
    	SimpleCard card = new SimpleCard();
    	card.setTitle("HelloWorld");
    	card.setContent(speechText);

    	// Create the plain text output.
    	PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    	speech.setText(speechText);

    	return SpeechletResponse.newTellResponse(speech, card);
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    protected SpeechletResponse getWelcomeResponse() {
    	String speechText = "";

    	// Create the Simple card content.
    	SimpleCard card = new SimpleCard();
    	card.setTitle("HelloWorld");
    	card.setContent(speechText);

    	// Create the plain text output.
    	PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    	speech.setText(speechText);

    	// Create reprompt
    	Reprompt reprompt = new Reprompt();
    	reprompt.setOutputSpeech(speech);

    	return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    public void onSessionEnded(final SessionEndedRequest request, final Session session)
    		throws SpeechletException {
    	log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
    			session.getSessionId());
    	// any cleanup logic goes here
    }
}