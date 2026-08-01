using AutoMapper;

namespace Backend_dotnet.Utilities.Mappers
{
    
    /// Extension methods for mapping
    
    public static class MappingExtensions
    {
        public static TDestination MapTo<TDestination>(this object source, IMapper mapper)
        {
            return mapper.Map<TDestination>(source);
        }

        public static IEnumerable<TDestination> MapToList<TDestination>(this IEnumerable<object> source, IMapper mapper)
        {
            return mapper.Map<IEnumerable<TDestination>>(source);
        }
    }
}