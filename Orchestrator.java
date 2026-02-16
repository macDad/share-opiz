
@Slf4j
@Component
public class Orchestrator {
  private Orchestrator() {}

  /**
   * Execute group.
   *
   * @param serviceName the service name
   * @param funcGroup the func group
   */
  public static final void executeGroup(
      String serviceName, List<GenericServiceFunction[]> funcGroup) {
    if (!ObjectUtils.isNullOrEmpty(funcGroup)) {
      List<SystemException> lst = new ArrayList<>();
      Iterator<GenericServiceFunction[]> var3 = funcGroup.iterator();

      while (var3.hasNext()) {
        GenericServiceFunction[] func = var3.next();

        try {
          execute(serviceName, func);
        } catch (SystemException var6) {
          log.error(var6.toString());
          lst.add(var6);
          break;
        }
      }

      if (!ObjectUtils.isNullOrEmpty(lst)) {
        ExceptionUtils.newMulSysEx(lst);
      }
    }
  }

  /**
   * Execute.
   *
   * @param serviceName the service name
   * @param funcs the funcs
   * @throws SystemException the system exception
   */
  public static final void execute(String serviceName, GenericServiceFunction... funcs)
      throws SystemException {
    execute(serviceName, TransactionType.NOT, ErrorHandlingType.GROUP, funcs);
  }

  /**
   * Execute.
   *
   * @param serviceName the service name
   * @param tranType the tran type
   * @param errType the err type
   * @param funcs the funcs
   * @throws SystemException the system exception
   */
  public static final void execute(
      String serviceName,
      TransactionType tranType,
      ErrorHandlingType errType,
      GenericServiceFunction... funcs)
      throws SystemException {
    if (tranType == null) {
      tranType = TransactionType.NOT;
    }

    if (errType == null) {
      errType = ErrorHandlingType.GROUP;
    }

    if (!ObjectUtils.isNullOrEmpty(funcs)) {
      try {
        List<SystemException> lst = new ArrayList<>();
        GenericServiceFunction[] var5 = funcs;
        int var6 = funcs.length;

        for (int var7 = 0; var7 < var6; ++var7) {
          GenericServiceFunction func = var5[var7];
          if (errType.isGroup()) {
            try {
              func.apply();
            } catch (ValidationException var11) {
              lst.add(var11);
            } catch (SystemException var12) {
              lst.add(var12);
            }
          } else {
            func.apply();
          }
        }

        if (!ObjectUtils.isNullOrEmpty(lst)) {
          try {
            ExceptionUtils.newMulSysEx(lst);
          } catch (Exception var10) {
            log.error(var10.toString());
            throw var10;
          }
        }
      } catch (Exception var13) {
        if (tranType.isSupport()) {
          TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        log.error(var13.toString());
        throw var13;
      }
    }
  }
}
public enum ErrorHandlingType {
  /** Group error handling type. */
  GROUP("G"),
  /** Single error handling type. */
  SINGLE("S");

  private final String type;

  ErrorHandlingType(String type) {
    this.type = type;
  }

  /**
   * Is group boolean.
   *
   * @return the boolean
   */
  public boolean isGroup() {
    return this.equals(GROUP);
  }

  /**
   * Is single boolean.
   *
   * @return the boolean
   */
  public boolean isSingle() {
    return this.equals(SINGLE);
  }
}
@FunctionalInterface
public interface GenericServiceFunction {
  /** Apply. */
  void apply();
}
   * Validate the authentication request
   *
   * @param authenticationRequest the authentication request
   */
  private void validationAuthenticationRequest(AuthenticationRequest authenticationRequest) {
    GenericServiceFunction[] func = {
      () -> validator.validateUsername(authenticationRequest),
      () -> validator.validatePassword(authenticationRequest)
    };
    Orchestrator.execute(
        "Request Access Token", TransactionType.NOT, ErrorHandlingType.GROUP, func);
  }
