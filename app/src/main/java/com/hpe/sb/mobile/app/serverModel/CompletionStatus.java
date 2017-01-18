package com.hpe.sb.mobile.app.serverModel;

/**
 * An enumeration of completion statuses. Refer to value documentation for details.
 *
 * @author: shai.nagar@hp.com
 * @date: 1/1/13
 */
public enum CompletionStatus {

    /**
     * OK == success
     * In the case of bulk of operations, it means that all operations in bulk have status OK
     */
    OK,

    /**
     * Indicates that an error occurred during an execution of an operation, and the operation failed.
     * In the case of bulk of operations it means that
     * at least one of operation's results has status FAILED
     */
    FAILED,

    /**
     * Indicates that the execution of an operation has been rejected by the corresponding engine or service. Any
     * attempt to retry should fail for the same reason. This status means that the operation is either not supported
     * or cannot be executed for resource management or performance reasons.
     */
    REJECTED,

    /**
     * Indicates that an operation temporarily could not be executed. Retry attempts might succeed depending on
     * environment and load changes.
     */
    TEMPORARILY_REJECTED
}
