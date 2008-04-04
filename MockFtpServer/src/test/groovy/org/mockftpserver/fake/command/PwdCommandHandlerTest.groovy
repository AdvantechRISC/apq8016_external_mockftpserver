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

import org.mockftpserver.test.AbstractGroovyTest
import org.mockftpserver.core.command.Command
import org.mockftpserver.core.command.CommandHandler
import org.mockftpserver.core.command.CommandNames
import org.mockftpserver.core.session.SessionKeys
import org.mockftpserver.fake.StubServerConfiguration
import org.mockftpserver.fake.user.UserAccount
import org.apache.log4j.Logger

/**
 * Tests for PwdCommandHandler
 * 
 * @version $Revision: $ - $Date: $
 *
 * @author Chris Mair
 */
class PwdCommandHandlerTest extends AbstractFakeCommandHandlerTest {

    def DIR = "/usr/abc"
    
    void testHandleCommand() {
        session.setAttribute(SessionKeys.CURRENT_DIRECTORY, DIR)
        serverConfiguration.setTextForReplyCode(ReplyCodes.PWD_OK, "dir={0}")
        commandHandler.handleCommand(createCommand([]), session)        
        assertSessionReply(ReplyCodes.PWD_OK, "dir=${DIR}")
	}
    
    void testHandleCommand_CurrentDirectoryNotSet() {
        testHandleCommand_MissingRequiredSessionAttribute()
    }

    //-------------------------------------------------------------------------
    // Helper Methods
    //-------------------------------------------------------------------------
    
	CommandHandler createCommandHandler() {
	    new PwdCommandHandler()
	}
	
    Command createValidCommand() {
        return new Command(CommandNames.PWD, [])
    }
    
//    /**
//     * Assert that the Username is stored in the session, depending on the value of isUsernameInSession.
//     * @param isUsernameInSession - true if the Username is expected in the session; false if it is not expected
//     */
//    private void assertUsernameInSession(boolean isUsernameInSession) {
//        def expectedValue = isUsernameInSession ? USERNAME : null
//        assert session.getAttribute(SessionKeys.USERNAME) == expectedValue
//    }
}