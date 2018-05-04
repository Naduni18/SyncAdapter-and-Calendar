package net.chmielowski.syncadaptertest;


import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AccountAuthenticator extends AbstractAccountAuthenticator {
    private final Context context;

    public AccountAuthenticator(final Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle editProperties(
            final AccountAuthenticatorResponse response, final String accountType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle addAccount(
            final AccountAuthenticatorResponse response, final String accountType,
            final String authTokenType, final String[] requiredFeatures,
            final Bundle options) throws NetworkErrorException {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, new Intent(context, LoginActivity.class));
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(
            final AccountAuthenticatorResponse response, final Account account,
            final Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getAuthToken(
            final AccountAuthenticatorResponse response, final Account account,
            final String authTokenType, final Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAuthTokenLabel(final String authTokenType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle updateCredentials(
            final AccountAuthenticatorResponse response, final Account account,
            final String authTokenType, final Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures(
            final AccountAuthenticatorResponse response, final Account account,
            final String[] features) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }
}
