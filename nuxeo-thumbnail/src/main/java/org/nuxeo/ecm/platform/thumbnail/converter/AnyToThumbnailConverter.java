/*
 * (C) Copyright 2013 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Thomas Roger
 */

package org.nuxeo.ecm.platform.thumbnail.converter;

import static org.nuxeo.ecm.platform.thumbnail.ThumbnailConstants.ANY_TO_PDF_TO_THUMBNAIL_CONVERTER_NAME;
import static org.nuxeo.ecm.platform.thumbnail.ThumbnailConstants.PDF_AND_IMAGE_TO_THUMBNAIL_CONVERTER_NAME;

import java.io.Serializable;
import java.util.Map;
import java.util.regex.Pattern;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.convert.api.ConversionException;
import org.nuxeo.ecm.core.convert.api.ConversionService;
import org.nuxeo.ecm.core.convert.extension.Converter;
import org.nuxeo.ecm.core.convert.extension.ConverterDescriptor;
import org.nuxeo.runtime.api.Framework;

/**
 * Base converter choosing the correct convert to generate a thumbnail according
 * to the Blob's mime type.
 *
 * @since 5.8
 */
public class AnyToThumbnailConverter implements Converter {

    public static final String PDF_MIME_TYPE = "application/pdf";

    public static final Pattern PDF_MIME_TYPE_PATTERN = Pattern.compile("application/.*pdf");

    public static final String ANY_TO_PDF_CONVERTER_NAME = "any2pdf";

    @Override
    public void init(ConverterDescriptor descriptor) {
    }

    @Override
    public BlobHolder convert(BlobHolder blobHolder,
            Map<String, Serializable> parameters) throws ConversionException {
        Blob sourceBlob;

        try {
            sourceBlob = blobHolder.getBlob();
        } catch (ClientException e) {
            throw new ConversionException("Unable to fetch Blob", e);
        }

        if (sourceBlob == null) {
            return null;
        }

        String mimeType = sourceBlob.getMimeType();
        if (mimeType == null) {
            return null;
        }

        ConversionService conversionService = Framework.getLocalService(ConversionService.class);

        String converterName = null;
        if ((mimeType.startsWith("image/") || PDF_MIME_TYPE_PATTERN.matcher(
                mimeType).matches())
                && conversionService.isConverterAvailable(
                        PDF_AND_IMAGE_TO_THUMBNAIL_CONVERTER_NAME, true).isAvailable()) {
            converterName = PDF_AND_IMAGE_TO_THUMBNAIL_CONVERTER_NAME;
        } else if (conversionService.isSourceMimeTypeSupported(
                ANY_TO_PDF_CONVERTER_NAME, mimeType)
                && conversionService.isConverterAvailable(
                        ANY_TO_PDF_CONVERTER_NAME, true).isAvailable()) {
            converterName = ANY_TO_PDF_TO_THUMBNAIL_CONVERTER_NAME;
        }
        return converterName == null ? null : conversionService.convert(
                converterName, blobHolder, parameters);
    }
}
