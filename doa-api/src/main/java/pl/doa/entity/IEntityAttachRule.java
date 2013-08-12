/**
 * 
 */
package pl.doa.entity;

/**
 * @author activey
 *
 */
public interface IEntityAttachRule<T extends IEntity> {

	public T attachEntity();
}
