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

import org.mockftpserver.fake.command.AbstractFakeCommandHandler
import org.mockftpserver.core.command.ReplyCodes

/**
 * CommandHandler for the USER command
 * 
 * @version $Revision: $ - $Date: $
 *
 * @author Chris Mair
 */
class UserCommandHandler extends AbstractFakeCommandHandler {

    protected void handle(Command command, Session session) {
        def username = getRequiredParameter(command, 0)
        
        // If the UserAccount is configured to not require password for login
        def userAccount = serverConfiguration.getUserAccount(username)
        if (userAccount && !userAccount.passwordRequiredForLogin) {
            session.setAttribute(SessionKeys.USER_ACCOUNT, userAccount)
            sendReply(session, ReplyCodes.USER_LOGGED_IN_OK)
        }
        else {
            session.setAttribute(SessionKeys.USERNAME, username)
            sendReply(session, ReplyCodes.USER_NEED_PASSWORD_OK)
        }
    }

}