import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Class to represent a "grammar" from a file of the form:
 * 
 * <pre>
 *    {
 *    &lt;left-hand-side&gt;
 *    production body ;
 *    production body ;
 *    ...
 *    }
 * </pre>
 * 
 * where the first symbol names the set of production bodies and production
 * bodies are separated by ';' (they may span lines)
 *
 * @author David M. Hansen
 * @version 2.0
 *
 *          Modified by DM Hansen 2/18/15 2.0 fixed "bug" that each production
 *          needed to be on one line and eliminated unnecessary use of regex,
 *          simplifying the FSA
 *
 * @see ParseSymbol
 */
public class Grammar
{
   /**
    * Build a grammar from the given input
    *
    * @param input
    *           is scanner from which to read the grammar
    *
    * @throws IOException
    *            if input stream is bad
    * */
   public Grammar(Scanner input) throws IOException
   {
      // REGEX patterns used to determine what sort of information is
      // on a line. We assume that the start and end patterns will
      // always be on a line by themselves and that left-hand-sides
      // will also be on a line by themself. But bodies of productions
      // might span lines. We will always end with a semi-colon
      final String START = "{";
      final String END = "}";
      final String BODY_END = ";";

      // The symbol table we'll populate
      _symbolTable = new HashMap<ParseSymbol, ArrayList<ArrayList<ParseSymbol>>>();

      // List of symbols we encounter in a given production
      ArrayList<ParseSymbol> symbolList = new ArrayList<ParseSymbol>();

      // The algorithm uses a simple finite-state-machine to parse the
      // input. These flags are used to tell what state we're in
      // while processing a particular grammar production.
      boolean readingAProduction = false;
      boolean readingBodies = false;

      // Used during parsing to represent parts of productions
      ParseSymbol leftHandSideSymbol = null;
      String currentSymbol = null;

      // Read the words of the file looking for productions.
      // When we see one, start parsing the production
      // and add the production to the symbol table.
      while (input.hasNext())
      {
         currentSymbol = input.next();

         // If we're not yet reading a production, check to see if the
         // current symbol is the production start symbol
         if (!readingAProduction)
         {
            readingAProduction = currentSymbol.equals(START);
         }
         // Otherwise see if the symbol is the production stop symbol.
         // If so, set the flag to indicate that we've hit the end of
         // a production, otherwise we've got the head or body of a
         // production
         else if (currentSymbol.equals(END))
         {
            readingAProduction = false;
            readingBodies = false;
         }
         else
         // Reading a production so this is the head or bodies of a production
         {
            // If we're not reading a body then this must be a new
            // production and we're looking at the left-hand-side so
            // start a new entry in the symbol table
            if (!readingBodies)
            {
               // Add this new left hand side to the symbol table
               // with an empty list of symbol lists
               leftHandSideSymbol = new ParseSymbol(currentSymbol);
               _symbolTable.put(leftHandSideSymbol,
                        new ArrayList<ArrayList<ParseSymbol>>());
               // Should now be reading production bodies
               readingBodies = true;

               // If this is the first left-hand-side we've seen
               // then it's also the start symbol for the grammar
               if (_startSymbol == null)
               {
                  _startSymbol = leftHandSideSymbol;
               }
            }
            else
            // Must be in the body of a production
            {
               // If the symbol is the end symbol, then we're done
               // reading a particular production body
               if (currentSymbol.equals(BODY_END))
               {
                  // END so we're done reading this body. Add
                  // the list of symbols (not including this
                  // end symbol) to the symbol table for the
                  // current left-hand-side
                  _symbolTable.get(leftHandSideSymbol).add(symbolList);

                  // Allocate a new list of symbols for the
                  // next production body we encounter
                  symbolList = new ArrayList<ParseSymbol>();
               }
               else
               // Not at the end, add this symbol to current list
               {
                  // If the symbol is the literal string "\n" we'll turn that
                  // into an actual newline
                  if (currentSymbol.equals("\n"))
                  {
                     symbolList.add(new ParseSymbol("\n"));
                  }
                  else
                  {
                     symbolList.add(new ParseSymbol(currentSymbol));
                  }
               }
            } // else

         } // else if

      } // while

   } // GrammarParser

   /**
    * @return the table of symbols and productions
    */
   public HashMap<ParseSymbol, ArrayList<ArrayList<ParseSymbol>>> getSymbolTable()
   {
      return _symbolTable;
   }

   /**
    * @return the start symbol for this grammar
    */
   public ParseSymbol getStartSymbol()
   {
      return _startSymbol;
   }

   /**
    * Simple test program to read a file and dump the start symbol and symbol
    * table found in that file
    *
    * @param args
    *           is filename containing grammar
    */
   public static void main(String[] args) throws IOException
   {
      Grammar aGrammar = null;

      // Open the given grammar file
      if (args.length == 0)
      {
         System.err.println("Usage: java Grammar <filename>");
         System.exit(1);
      }

      try
      {
         aGrammar = new Grammar(new Scanner(new File(args[0])));
      }
      catch (IOException e)
      {
         System.err.println("Error accessing file " + args[0]);
         System.exit(1);
      }

      System.out.println("Start is :" + aGrammar.getStartSymbol());
      System.out.println("Productions are :"
               + aGrammar.getSymbolTable().get(aGrammar.getStartSymbol()));
      System.out.println("Symbol table is :\n" + aGrammar.getSymbolTable());

   } // main

   // The start symbol for the grammar
   private ParseSymbol                                             _startSymbol;

   // The productions of the grammar where each ParseSymbol is mapped to
   // a list of ParseSymbols that define a production
   private HashMap<ParseSymbol, ArrayList<ArrayList<ParseSymbol>>> _symbolTable;

} // Grammar
