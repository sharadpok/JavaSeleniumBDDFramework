Feature: Validate Sauce Demo Website Launch

  Scenario: Verify page title after navigating to Sauce Demo site
    Given user launches the Sauce Demo website
    Then user prints the page title

  Scenario: Verify login page heading
    Given user launches the Sauce Demo website
    Then user prints the login page heading