/**
 * SameYConstraint.java
 * 
 * Revision History:<br>
 * Aug 6, 2008 jbjohns - File created
 * 
 * <p>
 * 
 * <pre>
 * This work is released under the BSD License:
 * (C) 2008 Sketch Recognition Lab, Texas A&amp;M University (hereafter SRL @ TAMU)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sketch Recognition Lab, Texas A&amp;M University
 *       nor the names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior written
 *       permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY SRL @ TAMU ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL SRL @ TAMU BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
package srl.recognition.constraint.confidence;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import srl.recognition.constraint.IConstrainable;


/**
 * Compute the confidence that the centers of two shapes share the same y value.
 * This constraint requires TWO {@link IConstrainable} shape.
 * 
 * @author jbjohns
 */
public class SameYConstraint extends AbstractConfidenceConstraint {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ladder.recognition.constraint.IConstraint#newInstance()
	 */
	@Override
	public SameYConstraint newInstance() {
		return new SameYConstraint();
	}
	
	/**
	 * Logger
	 */
	private static Logger log = LoggerFactory.getLogger(SameYConstraint.class);
	
	/**
	 * Name for this constraint
	 */
	public static final String NAME = "SameY";
	
	/**
	 * Description for this constraint
	 */
	public static final String DESCRIPTION = "Two shape centers have the same y coordinate";
	
	/**
	 * Number of parameters this constraint uses.
	 */
	public static final int NUM_PARAMETERS = 2;
	
	/**
	 * Default threshold for this constraint
	 */
	public static final double DEFAULT_THRESHOLD = 0.1;
	
	
	/**
	 * Construct the constraint using {@link #DEFAULT_THRESHOLD}.
	 */
	public SameYConstraint() {
		this(DEFAULT_THRESHOLD);
	}
	

	/**
	 * Construct the constraint with the given threshold
	 * 
	 * @param threshold
	 *            The threshold to use when solving this constraint
	 */
	public SameYConstraint(double threshold) {
		super(NAME, DESCRIPTION, NUM_PARAMETERS, threshold);
		this.setScaleParameters(true);
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ladder.recognition.constraint.IConstraint#solve()
	 */
	public double solve() {
		// require 2 shapes
		if (getParameters() == null
		    || getParameters().size() < getNumRequiredParameters()) {
			log.debug("Same Y requires " + getNumRequiredParameters()
			          + " shapes");
			return 0;
		}
		
		IConstrainable shape1 = getParameters().get(0);
		IConstrainable shape2 = getParameters().get(1);
		
		return solve(shape1, shape2);
	}
	

	/**
	 * Shorthand method (no list of shapes) for solving the confidence that the
	 * y-values for the centers of two shapes is the same.
	 * 
	 * @param shape1
	 *            The first shape
	 * @param shape2
	 *            The second shape
	 * @return The confidence that the y-values for the two shape centers is the
	 *         same
	 */
	public double solve(IConstrainable shape1, IConstrainable shape2) {
		
		// Set the parameters
		List<IConstrainable> parms = new ArrayList<IConstrainable>();
		parms.add(shape1);
		parms.add(shape2);
		setParameters(parms);
		
		double y1 = shape1.getBoundingBox().getCenterY();
		log.debug("y1 = " + y1);
		
		double y2 = shape2.getBoundingBox().getCenterY();
		log.debug("y2 = " + y2);
		
		double yDiff = Math.abs(y1 - y2);
		log.debug("diff in y = " + yDiff);
		
		double thisThreshold = 15;
		double avgParentSize = getAverageParentShapeSize();
		log.debug("Average parent size = " + avgParentSize);
		if (avgParentSize > 0) {
			thisThreshold = avgParentSize * getThreshold();
		}
		log.debug("m_threshold = " + getThreshold() + ", this threshold = "
		          + thisThreshold);
		
		double conf = solveConfidence(yDiff, thisThreshold);
		log.debug("conf = " + conf);
		
		return conf;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.ladder.recognition.constraint.confidence.AbstractConstraint#
	 * isClearlyFalse(double)
	 */
	@Override
	public boolean isClearlyFalse(double value) {
		return value < 0.25;
	}
}
