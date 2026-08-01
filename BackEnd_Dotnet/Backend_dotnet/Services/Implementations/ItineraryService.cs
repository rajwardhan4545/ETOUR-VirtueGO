using AutoMapper;
using Backend_dotnet.DTOs;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Backend_dotnet.Services.Interfaces;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Backend_dotnet.Services.Implementations
{
    public class ItineraryService : IItineraryService
    {
        private readonly IItineraryRepository _itineraryRepository;
        private readonly IMapper _mapper;

        public ItineraryService(IItineraryRepository itineraryRepository, IMapper mapper)
        {
            _itineraryRepository = itineraryRepository;
            _mapper = mapper;
        }

        public async Task<IEnumerable<ItineraryDto>> GetAllAsync()
        {
            var itineraries = await _itineraryRepository.GetAllAsync();
            return _mapper.Map<IEnumerable<ItineraryDto>>(itineraries);
        }

        public async Task<ItineraryDto> GetByIdAsync(int id)
        {
            var itinerary = await _itineraryRepository.GetByIdAsync(id);
            return _mapper.Map<ItineraryDto>(itinerary);
        }

        public async Task<IEnumerable<ItineraryDto>> GetByCategoryIdAsync(int categoryId)
        {
            var itineraries = await _itineraryRepository.GetByCategoryIdAsync(categoryId);
            return _mapper.Map<IEnumerable<ItineraryDto>>(itineraries);
        }

        public async Task<ItineraryDto> CreateAsync(ItineraryDto dto)
        {
            var itinerary = _mapper.Map<itinerary_master>(dto);
            var created = await _itineraryRepository.AddAsync(itinerary);
            return _mapper.Map<ItineraryDto>(created);
        }

        public async Task<ItineraryDto> UpdateAsync(int id, ItineraryDto dto)
        {
            var existing = await _itineraryRepository.GetByIdAsync(id);
            if (existing == null) return null;

            _mapper.Map(dto, existing);
            await _itineraryRepository.UpdateAsync(existing);
            return _mapper.Map<ItineraryDto>(existing);
        }

        public async Task<bool> DeleteAsync(int id)
        {
            return await _itineraryRepository.DeleteAsync(id);
        }

        public async Task ImportCsvAsync(IFormFile file)
        {
            using var stream = new StreamReader(file.OpenReadStream());
            using var csv = new CsvHelper.CsvReader(stream, System.Globalization.CultureInfo.InvariantCulture);

            // Skip header if present (CsvHelper does this automatically if configured, or we assume header exists)
            // Java implementation: skips 1 line. CsvHelper assumes header by default.

            var records = new List<itinerary_master>();
            
            // Read manually to handle custom mapping if needed, or use GetRecords with a class map
            // Java row format: [CategoryId, DayNo, Detail, Image]
            
            // We'll read manually to match Java's raw row parsing
            
            // csv.Read();
            // csv.ReadHeader();

            while (await csv.ReadAsync())
            {
                try
                {
                    // Assuming no header in the logic or default header matching. 
                    // Java code: Integer categoryId = Integer.parseInt(row[0]);
                    
                    var categoryId = csv.GetField<int>(0);
                    var dayNo = csv.GetField<int>(1);
                    var detail = csv.GetField<string>(2);
                    var image = csv.TryGetField<string>(3, out var img) ? img : null;

                    var itinerary = new itinerary_master
                    {
                        category_id = categoryId,
                        day_no = dayNo,
                        itinerary_detail = detail,
                        day_wise_image = image
                    };

                    try
                    {
                        await _itineraryRepository.AddAsync(itinerary);
                    }
                    catch (Exception)
                    {
                        // Duplicate or error - skip silently as per Java implementation
                        // System.out.println("duplicate skipped...");
                    }
                }
                catch (Exception)
                {
                   // Parse error or other row issue
                }
            }
        }
    }
}
