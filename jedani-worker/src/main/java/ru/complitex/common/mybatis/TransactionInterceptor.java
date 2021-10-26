package ru.complitex.common.mybatis;

import org.apache.ibatis.session.SqlSessionManager;
import org.apache.wicket.cdi.NonContextual;
import org.mybatis.cdi.SqlSessionManagerRegistry;
import org.mybatis.cdi.Transactional;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.*;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author Ivanov Anatoliy
 */
@Transactional
@Interceptor
public class TransactionInterceptor implements Serializable {
    @Inject
    private transient SqlSessionManagerRegistry registry;

    @Inject
    private transient Instance<UserTransaction> userTransaction;

    @AroundInvoke
    public Object invoke(InvocationContext ctx) throws Exception {
        Transactional transactional = getTransactionalAnnotation(ctx);
        boolean isInitiator = start(transactional);
        boolean isExternalJta = isTransactionActive();
        if (isInitiator && !isExternalJta) {
            beginJta();
        }
        boolean needsRollback = transactional.rollbackOnly();
        Object result;
        try {
            result = ctx.proceed();
        } catch (Exception ex) {
            Exception unwrapped = unwrapException(ex);
            needsRollback = needsRollback || needsRollback(transactional, unwrapped);
            throw unwrapped;
        } finally {
            if (isInitiator) {
                try {
                    if (needsRollback) {
                        rollback(transactional);
                    } else {
                        commit(transactional);
                    }
                } finally {
                    close();
                    endJta(isExternalJta, needsRollback);
                }
            }
        }
        return result;
    }

    private boolean needsRollback(Transactional transactional, Throwable throwable) {
        if (RuntimeException.class.isAssignableFrom(throwable.getClass())) {
            return true;
        }
        for (Class<?> exceptionClass : transactional.rollbackFor()) {
            if (exceptionClass.isAssignableFrom(throwable.getClass())) {
                return true;
            }
        }
        return false;
    }

    protected Transactional getTransactionalAnnotation(InvocationContext ctx) {
        Transactional t = ctx.getMethod().getAnnotation(Transactional.class);
        if (t == null) {
            t = ctx.getMethod().getDeclaringClass().getAnnotation(Transactional.class);
        }
        return t;
    }

    private boolean start(Transactional transactional) {
        boolean started = false;
        for (SqlSessionManager manager : getRegistry().getManagers()) {
            if (!manager.isManagedSessionStarted()) {
                manager.startManagedSession(transactional.executorType(),
                        transactional.isolation().getTransactionIsolationLevel());
                started = true;
            }
        }
        return started;
    }

    private void commit(Transactional transactional) {
        for (SqlSessionManager manager : getRegistry().getManagers()) {
            manager.commit(transactional.force());
        }
    }

    private void rollback(Transactional transactional) {
        for (SqlSessionManager manager : getRegistry().getManagers()) {
            manager.rollback(transactional.force());
        }
    }

    private void close() {
        for (SqlSessionManager manager : getRegistry().getManagers()) {
            manager.close();
        }
    }

    private Exception unwrapException(Exception wrapped) {
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else if (!(unwrapped instanceof Exception)) {
                return new RuntimeException(unwrapped);
            } else {
                return (Exception) unwrapped;
            }
        }
    }

    protected boolean isTransactionActive() throws SystemException {
        return getUserTransaction().get().getStatus() != Status.STATUS_NO_TRANSACTION;
    }

    protected void beginJta() throws NotSupportedException, SystemException {
        getUserTransaction().get().begin();
    }

    protected void endJta(boolean isExternalTransaction, boolean needsRollback)
            throws SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
        if (isExternalTransaction) {
            if (needsRollback) {
                getUserTransaction().get().setRollbackOnly();
            }
        } else {
            if (needsRollback) {
                getUserTransaction().get().rollback();
            } else {
                getUserTransaction().get().commit();
            }
        }
    }

    private SqlSessionManagerRegistry getRegistry() {
        if (registry == null) {
            NonContextual.of(this).inject(this);
        }

        return registry;
    }

    private Instance<UserTransaction> getUserTransaction() {
        if (userTransaction == null) {
            NonContextual.of(this).inject(this);
        }

        return userTransaction;
    }
}
