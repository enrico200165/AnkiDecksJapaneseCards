xquery version "1.0";
<locations>
	<NormalLocations>
		{
		for $location in /locations/location
		let $name := data($location)
		where $location[not(@popular) or @popular ne 'yes']
		order by $name
		return <location>{$name}</location>
		}
	</NormalLocations>
	<PopularLocations>
		{
		for $location in /locations/location
		let $name := data($location)
		where $location/@popular = 'yes'
		order by $name
		return <location>{$name}</location>
		}
	</PopularLocations>
</locations>
