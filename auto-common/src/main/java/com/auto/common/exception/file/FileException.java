package com.auto.common.exception.file;

import com.auto.common.exception.base.BaseException;

/**
 * 文件信息异常类
 * 
 * @author auto
 */
public class FileException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args)
    {
        super("file", code, args, null);
    }

}
