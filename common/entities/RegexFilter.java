package common.entities;

/**
 * [description]
 * <p>
 * Created on 2020.12.12.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class RegexFilter {
  private final String[] regexExpressions;
  private final String description;

  public RegexFilter(String[] regexExpressions, String description) {
    this.regexExpressions = regexExpressions;
    this.description = description;
  }

  public boolean matches(String strToMatch) {
    for (int i = 0; i < this.regexExpressions.length; i++) {
      if (!strToMatch.matches(this.regexExpressions[i])) {
        return false;
      }
    }
    return true;
  }
  
  public String[] getRegexExpressions() {
    return this.regexExpressions;
  }

  public String getDescription() {
    return this.description;
  }

  
}
