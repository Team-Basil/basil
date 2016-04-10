package basil;

import java.util.*;

import org.apache.commons.lang3.*;

/**
 * Holds the steps and current step number which is synchronized with firebase.
 */
public class Recipe {
	protected int currentStepNumber;
	protected List<String> steps;
	
	public Recipe() {
	}
	
	public void set(int currentStep, List<String> steps) {
		this.currentStepNumber = currentStep;
		this.steps = steps;
	}
	
	public void setCurrentStep(int currentStep) {
		this.currentStepNumber = currentStep;
	}
	
	public int getCurrentStepNumber() {
		return currentStepNumber;
	}
	
	public int getNumberOfSteps() {
		return steps.size();
	}
	
	public void updateCurrentStep(int inc) {
		currentStepNumber += inc;
		
		if(!hasStep(currentStepNumber)) {
			currentStepNumber -= inc;
		}
		
		if(currentStepNumber <= 0) currentStepNumber = 1;
	}
	
	public boolean hasStep(int stepNumber) {
		return steps.size() >= stepNumber;
	}
	
	public String getStep(int stepNumber) {
		return steps.get(stepNumber-1);
	}
	
	public String getCurrentStep() {
		if(hasStep(currentStepNumber)) return getStep(currentStepNumber);
		else return null;
	}
	
	public String getAllSteps() {
		return StringUtils.join(steps, ". ");
	}
}
