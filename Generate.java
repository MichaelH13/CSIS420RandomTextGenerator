import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * Class that is used to generate sentences given a text file. Grammar.java and
 * ParseSymbol.java are required to run this class.
 * 
 * @author 1825794
 *
 */
public class Generate
{
   /**
    * Your task is to write a text generator that performs the following
    * actions:
    * 
    * Reads a grammar file (provided as a command-line argument) Generates text
    * by starting with the start symbol and generating output by replacing each
    * non-terminal with a randomly chosen production for that non-terminal.
    * 
    * @param args
    *           The first index of this array is the name of the grammar file to
    *           generate sentences with.
    */
   public static void main(String[] args)
   {
      // Declare variables to generate our grammar sentences.
      Grammar generatingGrammar = null;
      ArrayDeque<ParseSymbol> stackSymbolsToProcess = new ArrayDeque<ParseSymbol>();
      HashMap<ParseSymbol, ArrayList<ArrayList<ParseSymbol>>> grammarSymbolTable;
      StringBuilder resultSentence = new StringBuilder();
      ArrayList<ParseSymbol> symbolsToProcess;
      Random randNumGenerator = new Random();

      // Get the grammar.
      try
      {
         // Create a new grammar using the Scanner wrapped around the
         // File created using the first argument provided.
         generatingGrammar = new Grammar(new Scanner(new File(args[0])));
      }
      catch (IOException e)
      {
         // Error message if there is a file.
         // Display an error message if there is an exception when
         // trying to access the file.
         System.err.println("Error accessing file " + args[0]);
         System.exit(1);
      }
      catch (ArrayIndexOutOfBoundsException e)
      {
         // Error message if there is no filename provided.
         // Display an error message if there is an out of bounds exception when
         // trying to access the file.
         System.err.println("Usage: java Generate [grammarfile]");
         System.exit(1);
      }

      // Get our table of ParseSymbols to use for generating sentences.
      grammarSymbolTable = generatingGrammar.getSymbolTable();

      // Add the initial ParseSymbol to the stack.
      stackSymbolsToProcess.push(generatingGrammar.getStartSymbol());

      // While we still have ParseSymbol to process.
      while (!stackSymbolsToProcess.isEmpty())
      {
         // If the current ParseSymbol is a terminal then just append it to our
         // result StringBuilder.
         if (stackSymbolsToProcess.peek().isTerminal())
         {
            // Get the current terminal ParseSymbol and append it to our
            // resultSentence and add a space after it for readability.
            resultSentence.append(stackSymbolsToProcess.pop() + " ");
         }
         else
         {
            // Get a list of the symbols that terminate the current non terminal
            // from the stack.
            symbolsToProcess = grammarSymbolTable.get(
                     stackSymbolsToProcess.peek()).get(
                     randNumGenerator.nextInt(grammarSymbolTable.get(
                              stackSymbolsToProcess.pop()).size()));

            // Iterate from right to left over the ArrayList of ParseSymbols we
            // randomly got from our ParseSymbol table. This will allow us to
            // process the remaining ParseSymbols in the correct order.
            for (int i = symbolsToProcess.size() - 1; i >= 0; i--)
            {
               // Push each ParseSymbol in the current list of ParseSymbols that
               // we randomly selected using the non-terminal ParseSymbol we are
               // currently resolving.
               stackSymbolsToProcess.push(symbolsToProcess.get(i));
            }
         }
      }

      // Display the resulting grammar sentence(s).
      System.out.println(resultSentence);
   }
}