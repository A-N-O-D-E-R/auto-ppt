/*
 * Vendored from Coreoz PPT-Templates (https://github.com/Coreoz/PPT-Templates),
 * branch master @ fb8b7386a9ad7dce9e139f4a6839c3037a142803, licensed Apache-2.0.
 * Only change: Lombok annotations replaced by plain Java. See NOTICE.
 */
package com.coreoz.ppt;

import java.util.Objects;

final class PptVariable {
	private final String name;
	private final String arg1;

	private PptVariable(String name, String arg1) {
		this.name = name;
		this.arg1 = arg1;
	}

	public static PptVariable of(String name, String arg1) {
		return new PptVariable(name, arg1);
	}

	public String getName() {
		return name;
	}

	public String getArg1() {
		return arg1;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof PptVariable variable
			&& Objects.equals(name, variable.name)
			&& Objects.equals(arg1, variable.arg1);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, arg1);
	}

	@Override
	public String toString() {
		return "PptVariable(name=" + name + ", arg1=" + arg1 + ")";
	}
}
