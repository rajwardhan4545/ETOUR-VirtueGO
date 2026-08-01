using AutoMapper;
using Backend_dotnet.DTOs;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Backend_dotnet.Services.Interfaces;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Backend_dotnet.Services.Implementations
{
    public class DepartureService : IDepartureService
    {
        private readonly IDepartureRepository _departureRepository;
        private readonly IMapper _mapper;

        public DepartureService(IDepartureRepository departureRepository, IMapper mapper)
        {
            _departureRepository = departureRepository;
            _mapper = mapper;
        }

        public async Task<IEnumerable<DepartureDto>> GetAllAsync()
        {
            var departures = await _departureRepository.GetAllAsync();
            return _mapper.Map<IEnumerable<DepartureDto>>(departures);
        }

        public async Task<DepartureDto> GetByIdAsync(int id)
        {
            var departure = await _departureRepository.GetByIdAsync(id);
            return _mapper.Map<DepartureDto>(departure);
        }

        public async Task<IEnumerable<DepartureDto>> GetByCategoryIdAsync(int categoryId)
        {
            var departures = await _departureRepository.GetByCategoryIdAsync(categoryId);
            return _mapper.Map<IEnumerable<DepartureDto>>(departures);
        }

        public async Task<DepartureDto> CreateAsync(DepartureDto dto)
        {
            var departure = _mapper.Map<departure_master>(dto);
            var created = await _departureRepository.AddAsync(departure);
            return _mapper.Map<DepartureDto>(created);
        }

        public async Task<DepartureDto> UpdateAsync(int id, DepartureDto dto)
        {
            var existing = await _departureRepository.GetByIdAsync(id);
            if (existing == null) return null;

            _mapper.Map(dto, existing);
            await _departureRepository.UpdateAsync(existing);
            return _mapper.Map<DepartureDto>(existing);
        }

        public async Task<bool> DeleteAsync(int id)
        {
            return await _departureRepository.DeleteAsync(id);
        }
    }
}
