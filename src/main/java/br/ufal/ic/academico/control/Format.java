package br.ufal.ic.academico.control;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Format {
    private Scanner input = new Scanner(System.in);

    public Format(){}

    public int inputSelect(int min, int max){
        System.out.print("Select: ");
        int i = stringToInt(input.nextLine());
        if (i < min || i > max) throw new InputMismatchException();
        else return i;
    }

    public void invalidInput(){
        System.out.print("Invalid input.");
        input.nextLine();
    }

    public void objNotFound(){
        System.out.print("Object not found.");
        input.nextLine();
    }

    public void operationAborted(){
        System.out.print("Operation aborted.");
        input.nextLine();
    }

    boolean intToBoolean(int i){
        if (i == 0) return false;
        else if (i == 1) return true;
        else throw new InputMismatchException();
    }

    public int stringToInt(String string){
        try {
            int i = Integer.parseInt(string.trim());
            return i;
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    double stringToDouble(String string){
        try {
            double i = Double.parseDouble(string.trim());
            if (i < 0) throw new InputMismatchException();
            else return i;
        } catch (NumberFormatException nfe) {
            throw new InputMismatchException();
        }
    }

    int intInterval(int num, int min, int max){
        if(num < min || num > max) throw new InputMismatchException();
        else return num;
    }


    public Object choose(List<Object> chooseList) {

        System.out.println("[0] - Cancel");
        for (int i = 1; i <= chooseList.size(); i++) {
            System.out.println("[" + i + "] - " + chooseList.get(i-1).toString());
        }

        int option = 0;

        try {
            option = new Format().inputSelect(0, chooseList.size());
            System.out.println();

        } catch (InputMismatchException e){
            new Format().invalidInput();
        }

        if (option == 0) return null;
        else {
            return chooseList.get(option-1);
        }
    }

}
