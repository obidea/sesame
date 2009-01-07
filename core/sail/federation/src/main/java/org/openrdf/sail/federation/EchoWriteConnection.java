package org.openrdf.sail.federation;

import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.store.StoreException;

/**
 * Echos all write operations to all members.
 * 
 * @author James Leigh
 */
abstract class EchoWriteConnection extends FederationConnection {

	public EchoWriteConnection(Federation federation, List<RepositoryConnection> members) {
		super(federation, members);
	}

	public void begin()
		throws StoreException
	{
		super.begin();
		excute(new Procedure() {

			public void run(RepositoryConnection member)
				throws StoreException
			{
				member.setAutoCommit(false);
			}
		});
	}

	public void rollback()
		throws StoreException
	{
		excute(new Procedure() {

			public void run(RepositoryConnection member)
				throws StoreException
			{
				member.rollback();
				member.setAutoCommit(true);
			}
		});
		super.rollback();
	}

	public void commit()
		throws StoreException
	{
		excute(new Procedure() {

			public void run(RepositoryConnection member)
				throws StoreException
			{
				member.commit();
				member.setAutoCommit(false);
			}
		});
		super.commit();
	}

	public void setNamespace(final String prefix, final String name)
		throws StoreException
	{
		excute(new Procedure() {

			public void run(RepositoryConnection member)
				throws StoreException
			{
				member.setNamespace(prefix, name);
			}
		});
	}

	public void clearNamespaces()
		throws StoreException
	{
		excute(new Procedure() {

			public void run(RepositoryConnection member)
				throws StoreException
			{
				member.clearNamespaces();
			}
		});
	}

	public void removeNamespace(final String prefix)
		throws StoreException
	{
		excute(new Procedure() {

			public void run(RepositoryConnection member)
				throws StoreException
			{
				member.removeNamespace(prefix);
			}
		});
	}

	public void removeStatements(final Resource subj, final URI pred, final Value obj,
			final Resource... contexts)
		throws StoreException
	{
		excute(new Procedure() {

			public void run(RepositoryConnection member)
				throws StoreException
			{
				member.removeMatch(subj, pred, obj, contexts);
			}
		});
	}

}