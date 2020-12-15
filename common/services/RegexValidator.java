package common.services;

/**
 * [description]
 * <p>
 * Created on 2020.12.12.
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class RegexValidator {
  private final String[] regexPolicies;
  private final String description;

  public RegexValidator(String[] regexPolicies, String description) {
    this.regexPolicies = regexPolicies;
    this.description = description;
  }

  public boolean matches(String strToMatch) {
    for (int i = 0; i < this.regexPolicies.length; i++) {
      if (!strToMatch.matches(this.regexPolicies[i])) {
        return false;
      }
    }
    return true;
  }
  
  public String[] getRegexPolicies() {
    return this.regexPolicies;
  }

  public String getDescription() {
    return this.description;
  }

  
}
