package net.mypapit.mobile.myposition;

/******************************************************************************
*
*							LatLonConvert.java
*
*******************************************************************************
* 
* Java Class: LatLonConvert
* 
*	This Java class is part of a collection of classes developed for the 
*	reading and processing of oceanographic and meterological data collected 
*	since 1970 by environmental buoys and stations.  This dataset is 
*	maintained by the National Oceanographic Data Center and is publicly 
*	available.  These Java classes were written for the US Environmental 
*	Protection Agency's National Exposure Research Laboratory under Contract 
*	No. GS-10F-0073K with Neptune and Company of Los Alamos, New Mexico.
* 
* Purpose:
*
*	This class contains utilities for latitude/longitude conversions.
*	Specifically, this class performs conversions from lat (or long) 
*	in decimal degrees to degrees-minutes-seconds, and vice versa.
*	
* Inputs:
*
*	Given a decimal degree, the equivalent degrees/minutes/seconds 
*		are calculated, and vice versa, using two methods.
* 
* Outputs:
*
*	The outputs are provided as public accessor methods:
*		double getDecimal()	returns decimal degrees
*		double getDegree()	returns degree part of degree/minute/second
*		double getMinute()	returns minute part of degree/minute/second
*		double getSecond()	returns second part of degree/minute/second
* 
* Required classes or packages:
*
*	Classes from the java language:
*	
*		java.lang.Math
*	
*	Classes developed as part of this project:
* 
*		None
* 
* Required functions or methods:
*
*   None
* 
* Package of which this class is a member:
*
*	default
* 
* Known limitations:
*
*	Values provided to this class as input are assumed to be valid, 
*	but the math doesn't care.
* 
* Compatibility:
*
*	Java 1.1.8
* 
* References:
*
*	None.
* 
* Author/Company:
*
* 	JDT: Neptune and Company
* 
* Change log:
*  
*	date       ver    by	description of change
*	_________  ____   ___	________________________________________________
*	06 Feb 01  0.01   JDT	Original coding.
*	07 Feb 01  0.02   JDT	Fixed conversion from negative Lat Long.
*	07 Feb 01  0.03   JDT	Fixed roundoff problems with sec.
*	12 Feb 01  0.04   JDT	Fixed roundoff problems with min.
*	27 Feb 01  0.05   JDT	Renamed LatLonUtil to LatLonConvert and 
*							  provided an overloaded constructor, and 
*							  accessor methods.
*	28 Mar 01  1.10   JDT	Final version accompanying deliverable 1b.
*	13 Aug 01  1.20   JDT	Final version accompanying deliverable 1c.
*	 4 Sep 01  1.21   JDT	Thorough code and comment review.
*	21 Sep 01  1.30   JDT	Final version accompanying deliverable 2.
*	17 Dec 01  1.40   JDT	Version accompanying final deliverable.
*					
* -------------------------------------------------------------------------- */

// Import required classes
import java.lang.Math;

/******************************************************************************
*	class:						LatLonConvert class
*******************************************************************************
*				
*	This class contains utilities for latitude/longitude conversions.
*				
* -------------------------------------------------------------------------- */
public class LatLonConvert
{
	// declare local variables used throughout the class
	private double dfDecimal;		// decimal degrees
	private double dfDegree;		// degree part of degrees/minutes/seconds
	private double dfMinute;		// minute part of degrees/minutes/seconds
	private double dfSecond;		// second part of degrees/minutes/seconds

	
/******************************************************************************
*	method:						LatLonConvert
*******************************************************************************
*				
*	The two constructors for LatLonConvert class accept either
*	
*	- a single double, which is interpreted as decimal degrees to be 
*		converted to degrees/minutes/seconds, or
*	
*	- three doubles, which are interpreted as values of degrees, minutes, 
*		and seconds, respectively, to be converted to decimal degrees.
*	
*	Member of LatLonConvert class
*				
* -------------------------------------------------------------------------- */

	// This constructor converts decimal degrees to degrees/minutes/seconds
	public LatLonConvert(
							double dfDecimalIn
						)
	{
		// load local variables
		dfDecimal = dfDecimalIn;

		// call appropriate conversion method
		fromDec2DMS();
	}

	// This constructor converts degrees/minutes/seconds to decimal degrees
	public LatLonConvert(
							double dfDegreeIn,
							double dfMinuteIn,
							double dfSecondIn
						)
	{
		// load local variables
		dfDegree = dfDegreeIn;
		dfMinute = dfMinuteIn;
		dfSecond = dfSecondIn;

		// call appropriate conversion method
		fromDMS2Dec();
	}

	
/******************************************************************************
*	method:					fromDec2DMS()
*******************************************************************************
*
*   Converts decimal degrees to degrees/minutes/seconds.
*				
*	Member of LatLonConvert class
*				
* -------------------------------------------------------------------------- */
	private void fromDec2DMS()
	{
		// define variables local to this method
		double dfFrac;			// fraction after decimal
		double dfSec;			// fraction converted to seconds

		// Get degrees by chopping off at the decimal
		dfDegree = Math.floor( dfDecimal );
		// correction required since floor() is not the same as int()
		if ( dfDegree < 0 )
			dfDegree = dfDegree + 1;

		// Get fraction after the decimal
		dfFrac = Math.abs( dfDecimal - dfDegree );

		// Convert this fraction to seconds (without minutes)
		dfSec = dfFrac * 3600;

		// Determine number of whole minutes in the fraction
		dfMinute = Math.floor( dfSec / 60 );

		// Put the remainder in seconds
		dfSecond = dfSec - dfMinute * 60;

		// Fix rounoff errors
		if ( Math.rint( dfSecond ) == 60 )
		{
			dfMinute = dfMinute + 1;
			dfSecond = 0;
		}

		if ( Math.rint( dfMinute ) == 60 )
		{
			if ( dfDegree < 0 )
				dfDegree = dfDegree - 1;
			else // ( dfDegree => 0 )
				dfDegree = dfDegree + 1;

			dfMinute = 0;
		}

		return;
	}

	
/******************************************************************************
*	method:					fromDMS2Dec()
*******************************************************************************
*
*   Converts degrees/minutes/seconds to decimal degrees.
*				
*	Member of LatLonConvert class
*				
* -------------------------------------------------------------------------- */
	private void fromDMS2Dec()
	{
		// define variables local to this method
		double dfFrac;					// fraction after decimal

		// Determine fraction from minutes and seconds
		dfFrac = dfMinute / 60 + dfSecond / 3600;

		// Be careful to get the sign right. dfDegIn is the only signed input.
		if ( dfDegree < 0 )
			dfDecimal = dfDegree - dfFrac;
		else
			dfDecimal = dfDegree + dfFrac;

		return;
	}

	
/******************************************************************************
*	method:					getDecimal()
*******************************************************************************
*
*   Gets the value in decimal degrees.
*				
*	Member of LatLonConvert class
*				
* -------------------------------------------------------------------------- */
	public double getDecimal()
	{
		return( dfDecimal );
	}

	
/******************************************************************************
*	method:					getDegree()
*******************************************************************************
*
*   Gets the degree part of degrees/minutes/seconds.
*				
*	Member of LatLonConvert class
*				
* -------------------------------------------------------------------------- */
	public double getDegree()
	{
		return( dfDegree );
	}

	
/******************************************************************************
*	method:					getMinute()
*******************************************************************************
*
*   Gets the minute part of degrees/minutes/seconds.
*				
*	Member of LatLonConvert class
*				
* -------------------------------------------------------------------------- */
	public double getMinute()
	{
		return( dfMinute );
	}

	
/******************************************************************************
*	method:					getSecond()
*******************************************************************************
*
*   Gets the second part of degrees/minutes/seconds.
*				
*	Member of LatLonConvert class
*				
* -------------------------------------------------------------------------- */
	public double getSecond()
	{
		return( dfSecond );
	}

	
}

/*-----------------------------------------------------------------------------
*							end of class
*----------------------------------------------------------------------------*/
