/* Copyright 2008 - 2021 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under GPL-3 License (see gpl.txt).
 */

package kdo.ebnDevKit.staubs.decision.beliefs;

import kdo.ebnDevKit.staubs.model.IStaubs;

/**
 * dirty belief
 * @author Thomas Rinklin
 *
 */
public class Dirty extends Belief
{
	public Dirty(String name, IStaubs model)
	{
		super(name, model);
	}

	@Override
	public float getTruthValue()
	{
		return (float) model.getDirt();
	}
}
