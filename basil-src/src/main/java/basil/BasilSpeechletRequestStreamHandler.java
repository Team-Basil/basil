package basil;

import java.util.*;

import com.amazon.speech.speechlet.lambda.*;

public class BasilSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

	private static final Set<String> supportedApplicationIds = new HashSet<String>();
    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
        //supportedApplicationIds.add("amzn1.echo-sdk-ams.app.828f086f-7fae-4f8f-96f2-7ae3fb586ecd");
        supportedApplicationIds.add("amzn1.echo-sdk-ams.app.c8a4b075-2969-481b-8c24-b077caab45d4");
    }

    public BasilSpeechletRequestStreamHandler() {
        super(new BasilSpeechlet(), supportedApplicationIds);
    }
}
