/*
 * Copyright 2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mockftpserver.fake.command

import org.mockftpserver.fake.ServerConfigurationAware
import org.mockftpserver.fake.ServerConfiguration
import org.mockftpserver.core.CommandSyntaxException
import org.mockftpserver.core.IllegalStateException
import org.mockftpserver.core.command.Command
import org.mockftpserver.core.command.CommandHandler
import org.mockftpserver.core.command.ReplyCodes
import org.mockftpserver.core.session.Session
import org.apache.log4j.Logger

/**
 * Abstract superclass for CommandHandler classes for the "Fake" server.
 * 
 * @version $Revision: $ - $Date: $
 *
 * @author Chris Mair
 */
abstract class AbstractFakeCommandHandler implements CommandHandler, ServerConfigurationAware {

     final Logger LOG = Logger.getLogger(this.class)
     ServerConfiguration serverConfiguration
     
     /**
      * Use template method to centralize and ensure common validation
      */
     void handleCommand(Command command, Session session) {
         assert serverConfiguration != null
         assert command != null
         assert session != null

         try {
             handle(command, session)
         }
         catch(CommandSyntaxException e) {
             LOG.warn("Error handling command: $command; ${e}")
             sendReply(session, ReplyCodes.COMMAND_SYNTAX_ERROR)
         }
         catch(IllegalStateException e) {
             LOG.warn("Error handling command: $command; ${e}")
             sendReply(session, ReplyCodes.ILLEGAL_STATE)
         }
     }
     
     /**
      * Subclasses must implement this
      */
     protected abstract void handle(Command command, Session session)
     
     // -------------------------------------------------------------------------
     // Utility methods for subclasses
     // -------------------------------------------------------------------------

     /**
      * Send a reply for this command on the control connection. 
      * 
      * The reply code is designated by the <code>replyCode</code> property, and the reply text  
      * is retrieved from the <code>replyText</code> ResourceBundle, using the reply code as the key.
      * 
      * @param session - the Session
      * @param replyCode - the reply code
      * @param args - the optional message arguments; defaults to []
      * 
      * @throws AssertionError - if session is null
      * 
      * @see MessageFormat
      */
     protected void sendReply(Session session, int replyCode, args = []) {
         assert session
         assertValidReplyCode(replyCode);
         
         String key = Integer.toString(replyCode);
         String text = serverConfiguration.getTextForReplyCode(replyCode)
         
         String textWithArgs = MessageFormat.format(text, args as Object[]);

         String replyTextToLog = (textWithArgs == null) ? "" : " " + textWithArgs;
         // TODO change to LOG.debug()
         LOG.info("Sending reply [" + replyCode + replyTextToLog + "]");
         session.sendReply(replyCode, textWithArgs);
     }
     
     /**
      * Assert that the specified number is a valid reply code
      * @param replyCode - the reply code to check
      * 
      * @throws AssertionError - if the replyCode is invalid
      */
     protected void assertValidReplyCode(int replyCode) {
         assert replyCode > 0, "The number [" + replyCode + "] is not a valid reply code"
     }

     /**
      * Return the value of the command's parameter at the specified index.
      * @param command - the Command
      * @param index - the index of the parameter to retrieve
      * @return the value of the command parameter
      * @throws CommandSyntaxException if the Command does not have a parameter at that index
      */
     protected String getRequiredParameter(Command command, int index) {
         String value = command.getParameter(index)
         if (!value) {
             throw new CommandSyntaxException("$command missing required parameter at index [$index]")
         }
         return value
     }
      
     /**
      * Return the value of the named attribute within the session.
      * @param session - the Session
      * @param name - the name of the session attribute to retrieve
      * @return the value of the named session attribute
      * @throws IllegalStateException - if the Session does not contain the named attribute
      */
     protected Object getRequiredSessionAttribute(Session session, String name) {
         Object value = session.getAttribute(name)
         if (value == null) {
             throw new IllegalStateException("Session missing required attribute [$name]")
         }
         return value
     }
      
}