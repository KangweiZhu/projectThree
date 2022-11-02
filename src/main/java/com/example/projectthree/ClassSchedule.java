package com.example.projectthree;

/**
 * ClassSchedule is the class that serve the function of storing all the fitnesses classes that this fitness chain has.
 * In this project all the FitnessClasses is stored in an array
 *
 * @author Michael Israel, Kangwei Zhu
 */
public class ClassSchedule {
    private FitnessClass[] fitnessClasses;
    private int numClasses;

    /**
     * Constructor method without parameter.
     * Initialize the fitnessClasses array to null and the number of classes to zero.
     */
    public ClassSchedule() {
        numClasses = 0;
        fitnessClasses = null;
    }

    /**
     * Constructor method with parameter. Giving values to the instance variable fitnessclasses and numClasses.
     *
     * @param fitnessClasses The array that store all the FitnessClass objects.
     * @param numClasses     The number of classes this fitness chain has.
     */
    public ClassSchedule(FitnessClass[] fitnessClasses, int numClasses) {
        this.fitnessClasses = fitnessClasses;
        this.numClasses = numClasses;
    }

    /**
     * This method is used when getting the number of classes.
     *
     * @return The number of classes that this fitness chain has.
     */
    public int getNumClasses() {
        return this.numClasses;
    }

    /**
     * This method is used when getting the array that hold all the fitnessClasses.
     *
     * @return The array that hold all fitnessClasses.
     */
    public FitnessClass[] getFitnessClasses() {
        return this.fitnessClasses;
    }

    /**
     * This method is used when displaying all the fitness class schedule.
     */
    public void printClassSchedule() {
        System.out.println("-Fitness classes loaded-");
        for (int i = 0; i < numClasses; i++) {
            System.out.println(fitnessClasses[i].toString());
        }
        System.out.println("-end of class list.");
    }

    /**
     * This method is used when we are checking whether a FitnessClass exists or not.
     *
     * @param checkClass the FitnessClass we want to check.
     * @return true if it exists, otherwise false.
     */
    public boolean isFitnessClassExist(FitnessClass checkClass) {
        String className = checkClass.getFitnessClassName();
        String instructor = checkClass.getInstructor();
        Location location = checkClass.getLocation();
        boolean nameFlag = false;
        boolean instructorFlag = false;
        boolean locationFlag = false;
        boolean findFlag = false;
        for (int i = 0; i < numClasses; i++) {
            if (fitnessClasses[i].equals(checkClass)) {
                return true;
            }
            if (fitnessClasses[i].getFitnessClassName().equalsIgnoreCase(className)) {
                nameFlag = true;
            }
            if (fitnessClasses[i].getInstructor().equalsIgnoreCase(instructor)) {
                instructorFlag = true;
            }
            if (fitnessClasses[i].getLocation().compareLocation(location) == 0) {
                locationFlag = true;
            }
        }
        if (!nameFlag) {
            System.out.println(className + " - class does not exist.");
            return false;
        } else if (!instructorFlag) {
            System.out.println(instructor + " - instructor does not exist.");
            return false;
        } else {
            System.out.println(className + " by " + instructor + " does not exist at " + location);
            return false;
        }
    }

    /**
     * Get a specific class from the array that hold all the FitnessClasses object
     *
     * @param fitnessClass The class we want to retrieve
     * @return null if not found, specific fitnessClass object if found.
     */
    public FitnessClass getFitnessClass(FitnessClass fitnessClass) {
        for (int i = 0; i < numClasses; i++) {
            if (fitnessClass.equals(fitnessClasses[i])) {
                return fitnessClasses[i];
            }
        }
        return null;
    }
}
