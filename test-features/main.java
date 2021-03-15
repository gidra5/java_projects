abstract class F {}

public record A(int a1, int a2) implements F {}

public sealed class B permits C, D {}

sealed interface C extends B permits E {}

public class main {
  
}
