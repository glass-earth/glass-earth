
using System;

namespace State
{
		public class AppState
		{
				public static AppState instance = new AppState ();
				public static ConfigState config;
				public static EarthState earth;
				public static LeapState leap;

				public AppState ()
				{
						AppState.config = new ConfigState ();
						AppState.earth = new EarthState ();
						AppState.leap = new LeapState ();
				}

				public static AppState getInstance ()
				{
						return AppState.instance;
				}

				public static ConfigState getConfig ()
				{
						return AppState.config;
				}

				public static EarthState getEarth ()
				{
						return AppState.earth;
				}

				public static LeapState getLeap ()
				{
						return AppState.leap;
				}

		}

}

