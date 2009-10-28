/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.extension.version.baselining;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.version.VersionServicePolicies;
import org.alfresco.repo.version.common.versionlabel.SerialVersionLabelPolicy;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;


/**
 * Simple version label policy that simply logs the input parameters and returns the default version number. 
 *
 * @author Peter Monks (peter.monks@alfresco.com)
 * @version $Id$
 */
public class LoggingVersionLabelsPolicy
    implements VersionServicePolicies.CalculateVersionLabelPolicy
{
    private final static Log log = LogFactory.getLog(LoggingVersionLabelsPolicy.class);
    
    private final static QName POLICY_CALCULATE_VERSION_LABEL = QName.createQName(NamespaceService.ALFRESCO_URI, "calculateVersionLabel");
    private final static QName TYPE_VERSION_BASELINED_CONTENT = QName.createQName("http://www.alfresco.com/custom/extension/version/baselining/1.0", "VersionBaselinedContent");

    private final SerialVersionLabelPolicy defaultPolicy;
    
    private PolicyComponent policyComponent = null;
    
    
    public LoggingVersionLabelsPolicy()
    {
        this.defaultPolicy = new SerialVersionLabelPolicy();
    }


    public void init()
    {
        policyComponent.bindClassBehaviour(POLICY_CALCULATE_VERSION_LABEL,
                                           TYPE_VERSION_BASELINED_CONTENT,
                                           new JavaBehaviour(this, "calculateVersionLabel"));
    }


    public void setPolicyComponent(final PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }


    
    /**
     * @see org.alfresco.repo.version.VersionServicePolicies.CalculateVersionLabelPolicy#calculateVersionLabel(org.alfresco.service.namespace.QName, org.alfresco.service.cmr.version.Version, int, java.util.Map)
     */
    public String calculateVersionLabel(final QName                     classRef,
                                        final Version                   preceedingVersion,
                                        final int                       versionNumber,
                                        final Map<String, Serializable> versionProperties) 
    {
        String classRefStr          = classRef          == null ? "(null)" : classRef.toString();
        String preceedingVersionStr = preceedingVersion == null ? "(null)" : preceedingVersion.getVersionLabel();
        String versionNumberStr     = Integer.toString(versionNumber);
        String versionPropertiesStr = mapToString(versionProperties);
        
        log.info("LoggingVersionLabelsPolicy.calculateVersionLabel(" +
                  "\n\tclassRef          = " + classRefStr          + "," +
                  "\n\tpreceedingVersion = " + preceedingVersionStr + "," +
                  "\n\tversionNumber     = " + versionNumberStr     + "," +
                  "\n\tversionProperties = " + versionPropertiesStr + ")");
        
        return(defaultPolicy.calculateVersionLabel(classRef, preceedingVersion, versionNumber, versionProperties));
    }
    
    
    private String mapToString(final Map<String, Serializable> map)
    {
        StringBuffer result = new StringBuffer();
        
        if (map != null)
        {
            result.append('[');
            
            for (String key : map.keySet())
            {
                result.append(key);
                result.append('=');
                result.append(map.get(key));
                result.append(',');   // Note: this leaves a dangling comma at the end of the list, but I can't be bothered fixing it.  ;-)
            }

            result.append(']');
        }
        else
        {
            result.append("(null)");
        }
        
        return(result.toString());
    }

}
