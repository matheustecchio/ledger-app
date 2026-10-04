import { useQuery } from "@apollo/client";
import { accountTypeLabels, CreateAccountForm } from "./CreateAccountForm";
import { ACCOUNTS_QUERY, type AccountsData } from "./accounts.graphql";

function formatMoney(amount: string, currency: string) {
  const match = /^(-?)(\d+)(?:\.(\d+))?$/.exec(amount);
  if (!match) {
    throw new Error("Invalid decimal amount");
  }

  const [, sign, integer, fraction = ""] = match;
  const formatter = new Intl.NumberFormat(undefined, {
    style: "currency",
    currency,
    maximumFractionDigits: 4,
  });
  const minimumFractionDigits = formatter.resolvedOptions().minimumFractionDigits ?? 0;
  let displayFraction = fraction.padEnd(4, "0");

  while (displayFraction.length > minimumFractionDigits && displayFraction.endsWith("0")) {
    displayFraction = displayFraction.slice(0, -1);
  }

  const groupedInteger = new Intl.NumberFormat(undefined, {
    maximumFractionDigits: 0,
  }).formatToParts(BigInt(integer)).filter(({ type }) => type === "integer" || type === "group");
  let insertedInteger = false;

  return formatter.formatToParts(sign === "-" ? -1n : 1n).flatMap((part) => {
    if (part.type === "integer" || part.type === "group") {
      if (insertedInteger) {
        return [];
      }
      insertedInteger = true;
      return groupedInteger;
    }
    if (part.type === "fraction") {
      return [{ ...part, value: displayFraction }];
    }
    return [part];
  }).map(({ value }) => value).join("");
}

export function AccountsPage() {
  const { data, loading, error } = useQuery<AccountsData>(ACCOUNTS_QUERY);

  return (
    <section className="page">
      <header className="page-header">
        <div>
          <p className="eyebrow">Your money</p>
          <h1>Accounts</h1>
          <p>Keep each place you hold money visible in one ledger.</p>
        </div>
        <span className="live-pill">GraphQL connected</span>
      </header>

      <div className="panel create-panel">
        <div>
          <p className="eyebrow">New account</p>
          <h2>Add a financial account</h2>
        </div>
        <CreateAccountForm />
      </div>

      <div className="accounts-section">
        <div className="section-heading">
          <h2>Your accounts</h2>
          <span>{data?.accounts.length ?? 0} total</span>
        </div>
        {loading && <p className="state-message">Loading accounts…</p>}
        {error && <p className="state-message error" role="alert">Could not load accounts.</p>}
        {!loading && !error && data?.accounts.length === 0 && (
          <div className="empty-state">
            <span aria-hidden="true">◎</span>
            <h3>No accounts yet</h3>
            <p>Create your first account to prove the full Ledger data path.</p>
          </div>
        )}
        <div className="account-grid">
          {data?.accounts.map((account) => (
            <article className="account-card" key={account.id}>
              <div className="account-icon" aria-hidden="true">
                {account.name.slice(0, 1).toUpperCase()}
              </div>
              <div>
                <h3>{account.name}</h3>
                <p>{accountTypeLabels[account.type]} · {account.currency}</p>
              </div>
              <strong>{formatMoney(account.initialBalance, account.currency)}</strong>
            </article>
          ))}
        </div>
      </div>
    </section>
  );
}
