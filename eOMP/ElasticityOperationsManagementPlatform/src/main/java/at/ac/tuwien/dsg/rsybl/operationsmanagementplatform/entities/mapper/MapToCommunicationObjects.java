/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup E184.               
   
   This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
 
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/**
 *  Author : Georgiana Copil - e.copil@dsg.tuwien.ac.at
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.mapper;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Interaction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Message;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Responsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Role;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IInteraction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IMessage;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IResponsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Georgiana
 */
public class MapToCommunicationObjects {
    public static Role mapFromRole(IRole irole){
        Role role = new Role();
        role.setId(irole.getId());
        role.setAuthority(irole.getAuthority());
        role.setRoleName(irole.getRoleName());
        Set<IResponsibility> resp = irole.getResponsabilities();
        for (IResponsibility r:resp){
            role.addResponsability(mapFromResponsibility(r));
        }
        return role;
    }
    public static Responsibility mapFromResponsibility(IResponsibility iResponsibility){
        Responsibility resp = new Responsibility();
        resp.setId(iResponsibility.getId());
        resp.setAssociatedMetricPatterns(iResponsibility.getAssociatedMetricPatterns());
        resp.setAssociatedMetrics(iResponsibility.getAssociatedMetrics());
        resp.setResponsabilityType(iResponsibility.getResponsabilityType());
        resp.setResponsibilityName(iResponsibility.getResponsibilityName());
        return resp;
    }
    public static Interaction mapFromInteraction(IInteraction iInteraction){
     Interaction interaction = new Interaction();
     interaction.setDialogUuid(iInteraction.getDialogUuid());
     interaction.setId(iInteraction.getId());
     interaction.setInitiationDate(iInteraction.getInitiationDate());
     interaction.setInitiator(mapFromRole(iInteraction.getInitiator()));
     interaction.setReceiver(mapFromRole(iInteraction.getReceiver()));
     interaction.setType(iInteraction.getType());
     interaction.setUuid(iInteraction.getUuid());
     interaction.setMessage(mapFromMessage(iInteraction.getMessage()));
     return interaction;
    }
    public static Message mapFromMessage(IMessage iMessage){
        Message message = new Message();
        message.setActionEnforced(iMessage.getActionEnforced());
        message.setCause(iMessage.getCause());
        message.setCloudServiceId(iMessage.getCloudServiceId());
        message.setDescription(iMessage.getDescription());
        message.setId(iMessage.getId());
        if (iMessage.getInteraction()!=null)
        {
            message.setInteraction(mapFromInteraction(iMessage.getInteraction()));
        }
        message.setPriority(iMessage.getPriority());
        message.setTargetPartId(iMessage.getTargetPartId());
        message.setUuid(iMessage.getUuid());
        message.setValues(iMessage.getValues());
        return message;
    }
}
