/*
 * (C) Copyright 2013 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     dmetzler
 */
package org.nuxeo.ecm.automation.rest.jaxrs;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;

/**
 * Web object that allows to deal with documents list.
 *
 * @since 5.7.3
 */
@WebObject(type = "bulk")
public class BulkDocumentsObject extends DefaultObject {

    protected DocumentModelList docs;

    @Override
    public <A> A getAdapter(Class<A> adapter) {
        if (adapter == DocumentModel.class) {
            return adapter.cast(docs);
        }
        return super.getAdapter(adapter);
    }

    @Override
    public void initialize(Object... args) {
        assert args != null && args.length == 1;
        docs = (DocumentModelList) args[0];
    }

    @GET
    public DocumentModelList doGet() {
        return docs;
    }

    @DELETE
    public Response doDelete() throws ClientException {
        CoreSession session = getContext().getCoreSession();
        for (DocumentModel doc : docs) {
            session.removeDocument(doc.getRef());
        }
        return Response.ok().build();
    }

}