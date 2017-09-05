package co.fddittmar.planbuilder.data.model;

import java.io.Serializable;

/**
 * Exercise POJO.
 */

public class Exercise implements Serializable {

    private int id;
    private String name; //name of the exercise
    private int reps; //# of reps
    private int weight; //weight in kg

    public Exercise(){}

    public Exercise(String name, int reps, int weight) {
        this.name = name;
        this.reps = reps;
        this.weight = weight;
    }

    public Exercise(int id, String name, int reps, int weight) {
        this.id = id;
        this.name = name;
        this.reps = reps;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getReps() {
        return reps;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Name: " + getName() + ", Reps: " + getReps() + ", Weight: " + getWeight();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
