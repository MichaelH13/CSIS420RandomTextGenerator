import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Simple class representing terminal and non-terminal
 * grammar symbols
 *
 * @author David M. Hansen
 * @version 1.1
 */
public class ParseSymbol
{
   /**
    * @param value is the string value of the symbol
    */
   public ParseSymbol(String value)
   {
      _value = value;
   }


   /**
    * @return true if this is a non-terminal symbol
    */
   public boolean isNonTerminal()
   {
      // See if the pattern matches the NON_TERM pattern
      return NON_TERM.matcher(_value).find();
   }


   /**
    * @return true if this is a terminal symbol
    */
   public boolean isTerminal()
   {
      // If not a non-terminal, then a terminal
      return !isNonTerminal();
   }


   /**
    * @return symbol value
    */
   public String toString()
   {
      return _value;
   }


   /**
    * @return true if the symbol's strings are equal
    */
   public boolean equals(Object o)
   {
      // Nothing is equal to null, otherwise test values
      return (o == null ? false : _value.equals(((ParseSymbol) o)._value));
   }


   /**
    * @return HashCode from our string value
    */
   public int hashCode()
   {
      return _value.hashCode();
   }


   // Pattern for matching non-terminal symbols that begin with <, end with >
   private final static Pattern NON_TERM = Pattern.compile("^<.*>$");

   // The value of this symbol
   private String _value;
}
