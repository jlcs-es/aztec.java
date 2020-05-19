/**
 * Copyright (c) [2018] [ The Semux Developers ]
 * Copyright (c) [2016] [ <ether.camp> ]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.vm.crypto.zksnark;

/**
 * Interface of abstract finite field
 *
 * @author Mikhail Kalinin
 * @since 05.09.2017
 */
interface Field<T> {

    T add(T o);

    T mul(T o);

    T sub(T o);

    T squared();

    T dbl();

    T inverse();

    T negate();

    boolean isZero();

    boolean isValid();
}
