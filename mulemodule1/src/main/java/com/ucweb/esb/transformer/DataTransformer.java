package com.ucweb.esb.transformer;

import com.ucweb.esb.exception.DataTransformerException;

/**
 * Created by ggd543 on 14-1-15.
 */
public interface DataTransformer {
    public Object doTransform(Object src ,String encoding) throws DataTransformerException;
}
