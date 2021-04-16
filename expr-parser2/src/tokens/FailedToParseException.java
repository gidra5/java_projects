package tokens;

public class FailedToParseException extends Exception {
  private static final long serialVersionUID = 1L;

  public FailedToParseException() {
    super();
  }

  public FailedToParseException(String errmsg) {
      super(errmsg);
  }
}
