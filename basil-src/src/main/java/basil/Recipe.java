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
	
	// Returns true if the step is valid, false otherwise.
	public boolean setCurrentStep(int currentStep) {
		if(currentStep < 1) return false;
		if(currentStep > steps.size()) return false;
		
		this.currentStepNumber = currentStep;
		return true;
	}
	
	public int getCurrentStepNumber() {
		return currentStepNumber;
	}
	
	public int getNumberOfSteps() {
		return steps.size();
	}
	
	// Returns true if it could move the step 
	public boolean updateCurrentStep(int inc) {
		currentStepNumber += inc;
		
		if(!hasStep(currentStepNumber)) {
			currentStepNumber -= inc;
			return false;
		}
		
		if(currentStepNumber <= 0) {
			currentStepNumber = 1;
			return false;
		}
		
		return true;
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
