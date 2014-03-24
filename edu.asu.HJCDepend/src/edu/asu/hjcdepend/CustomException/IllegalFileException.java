package edu.asu.hjcdepend.CustomException;

public class IllegalFileException extends Exception{
	 public IllegalFileException() {}

     //Constructor that accepts a message
     public IllegalFileException(String message)
     {
        super(message);
     }

}
