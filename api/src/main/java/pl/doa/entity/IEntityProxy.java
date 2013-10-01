/**
 * 
 */
package pl.doa.entity;

/**
 * @author activey
 *
 */
public interface IEntityProxy<T extends IEntity> {

	public T get();
}
