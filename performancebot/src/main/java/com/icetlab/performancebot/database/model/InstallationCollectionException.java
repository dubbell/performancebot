package com.icetlab.performancebot.database.model;

/**
 * A custom exception class for the collection Installation, which is used when bad requests send to the database
 */
public class InstallationCollectionException extends Exception {

    /**
     * The message of the exception when doing an operation on non-existing installation
     */
    public final static String NO_SUCH_INSTALLATION = "No such installation in the database";

    /**
     * The message of the exception when doing an operation on non-existing GitHub repository
     */
    public final static String NO_SUCH_REPO = "No such GitHub repository in the database";

    /**
     * The message of the exception when doing an operation on non-existing benchmark method
     */
    public final static String NO_SUCH_METHOD = "No such benchmark method in the database";

    /**
     * The message of the exception when doing adding an already existing installation
     */
    public final static String INSTALLATION_EXISTS = "The installation already exists in the database";

    /**
     * The message of the exception when doing adding an already existing GitHub repository
     */
    public final static String REPO_EXISTS = "The GitHub repository already exists in the database";

    /**
     * The message of the exception when doing adding an already existing benchmark method
     */
    public final static String METHOD_EXISTS = "The benchmark method already exists in the database";

    private InstallationCollectionException(String message) {
        super(message);
    }

    /**
     * A method for getting an instance of InstallationCollectionException
     * @param msg The message that the exception should have
     * @return InstallationCollectionException object
     */
    public static InstallationCollectionException raiseException(String msg) {
        String message = "";
        if (msg.equals(NO_SUCH_INSTALLATION) || msg.equals(NO_SUCH_REPO)
                || msg.equals(NO_SUCH_METHOD) || msg.equals(INSTALLATION_EXISTS)
                || msg.equals(REPO_EXISTS) || msg.equals(METHOD_EXISTS)) {
            message = msg;
        }

        return new InstallationCollectionException(message);
    }
}