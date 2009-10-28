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
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;


/**
 * Custom version labeling policy that allows the base (starting) version number for an asset to be set
 * manually by the contributor via metadata.
 *
 * @author Peter Monks (peter.monks@alfresco.com)
 * @version $Id$
 */
public class BaselinedVersionLabelsPolicy
    implements VersionServicePolicies.BeforeCreateVersionPolicy,
               VersionServicePolicies.CalculateVersionLabelPolicy
{
    private final static Log log = LogFactory.getLog(BaselinedVersionLabelsPolicy.class);
    
    private final static String NAMESPACE_VERSION_BASELINING = "http://www.alfresco.com/custom/extension/version/baselining/1.0";
    
    private final static QName POLICY_BEFORE_CREATE_VERSION   = QName.createQName(NamespaceService.ALFRESCO_URI, "beforeCreateVersion");
    private final static QName POLICY_CALCULATE_VERSION_LABEL = QName.createQName(NamespaceService.ALFRESCO_URI, "calculateVersionLabel");
    private final static QName TYPE_VERSION_BASELINED_CONTENT = QName.createQName(NAMESPACE_VERSION_BASELINING,  "VersionBaselinedContent");
    private final static QName PROPERTY_BASE_VERSION_NUMBER   = QName.createQName(NAMESPACE_VERSION_BASELINING,  "BaseVersion");

    private final SerialVersionLabelPolicy defaultPolicy = new SerialVersionLabelPolicy();
    private final ThreadLocal<NodeRef>     savedNodeRef  = new ThreadLocal<NodeRef>();
    
    private NodeService     nodeService     = null;
    private PolicyComponent policyComponent = null;
    


    public void init()
    {
        policyComponent.bindClassBehaviour(POLICY_BEFORE_CREATE_VERSION,
                                           TYPE_VERSION_BASELINED_CONTENT,
                                           new JavaBehaviour(this, "beforeCreateVersion"));
        
        policyComponent.bindClassBehaviour(POLICY_CALCULATE_VERSION_LABEL,
                                           TYPE_VERSION_BASELINED_CONTENT,
                                           new JavaBehaviour(this, "calculateVersionLabel"));
    }
    
    
    public void setNodeService(final NodeService nodeService)
    {
        this.nodeService = nodeService;
    }


    public void setPolicyComponent(final PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }


    
    /**
     * @see org.alfresco.repo.version.VersionServicePolicies.BeforeCreateVersionPolicy#beforeCreateVersion(org.alfresco.service.cmr.repository.NodeRef)
     */
    public void beforeCreateVersion(final NodeRef nodeRef)
    {
        // This is a horribly nasty hack, but until ETHREEOH-3183 is fixed there's no real way around it.
        savedNodeRef.set(nodeRef);
    }


    /**
     * @see org.alfresco.repo.version.VersionServicePolicies.CalculateVersionLabelPolicy#calculateVersionLabel(org.alfresco.service.namespace.QName, org.alfresco.service.cmr.version.Version, int, java.util.Map)
     */
    public String calculateVersionLabel(final QName                     classRef,
                                        final Version                   preceedingVersion,
                                        final int                       versionNumber,
                                        final Map<String, Serializable> versionProperties) 
    {
        String result = null;
        
        
        // Only set the base version if there's no prior version number (ie. when versioning is first enabled)
        if (preceedingVersion == null)
        {
            NodeRef nodeRef = savedNodeRef.get();
            
            if (nodeRef != null)
            {
                String baseVersion = (String)nodeService.getProperty(nodeRef, PROPERTY_BASE_VERSION_NUMBER);
                
                if (baseVersion != null && baseVersion.trim().length() > 0)
                {
                    result = baseVersion;
                    savedNodeRef.remove();
                }
            }
        }
        
        if (result == null)
        {
            result = defaultPolicy.calculateVersionLabel(classRef, preceedingVersion, versionNumber, versionProperties);
        }
        
        return(result);
    }

}
