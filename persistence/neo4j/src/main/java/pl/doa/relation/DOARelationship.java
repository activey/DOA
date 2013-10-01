/*******************************************************************************
 * Copyright 2011 Inhibi Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are
 * permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright 
 * notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright 
 * notice, this list
 *        of conditions and the following disclaimer in the documentation 
 * and/or other materials
 *        provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY INHIBI LTD ``AS IS'' AND ANY 
 * EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * INHIBI LTD OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation 
 * are those of the authors and should not be interpreted as representing 
 * official policies, either expressed or implied, of Inhibi Ltd.
 *
 * Contributors:
 *    Inhibi Ltd - initial API and implementation
 *******************************************************************************/
/**
 *
 */
package pl.doa.relation;

import org.neo4j.graphdb.RelationshipType;

/**
 * @author activey
 */
public enum DOARelationship implements RelationshipType {

    HAS_ENTITY,    //

    HAS_ANCESTOR,

    HAS_ENTITY_REFERENCE,

    HAS_ATTRIBUTE,

    HAS_DEFINITION,   //

    HAS_FIELD,

    HAS_FIELD_DEFINITION,

    IS_STARTED_BY,    //

    HAS_INPUT,      //
    //
    HAS_OUTPUT,

    HAS_RUNNING_SERVICE, //

    HAS_INPUT_DEFINITION,  //

    HAS_OUTPUT_DEFINITION,   //

    HAS_TO_DEFINITION,    //

    HAS_FROM_DEFINITION,    //

    HAS_ARTIFACT_ENTITY,     //

    HAS_ARTIFACT_DEPENDENCY,   //

    HAS_ARTIFACT_RESOURCE,       //

    HAS_ARTIFACT_BASE_CONTAINER,

    HAS_EVENT_SOURCE,

    HAS_LISTENER_EVENT_SOURCE,

    HAS_EVENT_RECEIVER
}
