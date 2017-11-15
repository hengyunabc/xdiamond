package io.github.xdiamond.client.exception;

/**
 * Created with IntelliJ IDEA.
 *
 * @author fxltsbl3855
 *      XDiamond Exception
 */
public class XDException extends Exception {

    public XDException(){
        super();
    }

    public XDException(String msg){
        super(msg);
    }

    public XDException(String msg, Throwable t){
        super(msg,t);
    }

    public XDException(Throwable t){
        super(t);
    }
}

