# Contribution

This is a first version of a contribution file.
The guidelines should be followed to help create a common code style. This makes it easier to understand each others code and add contributions.

All text in this repository should be in English. This includes code, commit messages, review comments etc. 

## Java Code

Overall, the code should be easy to follow with descriptive names of classes, methods and variables that helps clarify what it represents. Try to use full words unless using common abbreviations. 

For java code, follow conventions for naming classes, methods and variables:
1. **Class names:** Nouns using (Upper)CamelCase.
2. **Method names:** Verbs using (lower)camelCase. 
3. **Variable names:** (lower)camelCase. 

### Comments

All classes, methods and variables should be commented using **Javadoc**. This mainly applies to everything that is public but could be used for private things as well to help clarify. Do not use author tags.

Inline comments in methods could be used if this helps to understand the code better. 

### Formatter

Use Google Java Style formatter as described in the READ-ME file.  

## Github

### Branches

Create one branch for each TODO. The name of the branch should be the same as the TODO on trello, but with dashes instead of spaces.
E.g: "Todo nr 2" becomes "todo-nr-2". 

### Commits

Try to make one commit per feature which let's you write a short commit message.
Commit messages should be informative of the changes that has been made to make it easier to find possible problems. Avoid short general commit messages such as "bug fix". Also, try to remember to write them in **imperative form** (e.g "add feature" instead of "added feature"). 

### Pull request

Before you make a pull request make sure to check off this list.

* The code is unit and integration tested.
* The code follows the guidelines in this document:
    * Valid Javadoc comments on all public classes, methods and variables.
    * Code is formatted.
* Add a description to the pull request which makes it easy for the reviewer to understand what you have done.

At least one person should review your pull request before it is merged.
