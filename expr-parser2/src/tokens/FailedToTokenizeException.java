package tokens;

public class FailedToTokenizeException extends Exception {
  private static final long serialVersionUID = 1L;

  public FailedToTokenizeException() {
    super();
  }

  public FailedToTokenizeException(String errmsg) {
      super(errmsg);
  }
}
