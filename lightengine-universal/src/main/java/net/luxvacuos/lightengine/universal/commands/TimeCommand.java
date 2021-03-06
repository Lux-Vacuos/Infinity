/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2019 Lux Vacuos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.luxvacuos.lightengine.universal.commands;

import java.io.PrintStream;

import net.luxvacuos.lightengine.universal.core.IWorldSimulation;

public class TimeCommand extends SimpleCommand {

	private IWorldSimulation worldSimulation;

	public TimeCommand(IWorldSimulation worldSimulation) {
		super("/time");
		this.worldSimulation = worldSimulation;
	}

	@Override
	public void execute(PrintStream out, Object... data) {
		if (data.length == 0) {
			out.println("No Option selected");
			return;
		}
		String param = (String) data[0];
		switch (param) {
		case "set":
			worldSimulation.setTime(Integer.parseInt((String) data[1]));
			out.println("Time set to: " + data[1]);
			break;
		case "time":
			out.println("Time is: " + worldSimulation.getTime());
			break;
		}
	}

}
