using AutoMapper;
using Backend_dotnet.DTOs;
using Backend_dotnet.Models.Entities;
using Backend_dotnet.Repositories.Interfaces;
using Backend_dotnet.Services.Interfaces;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Backend_dotnet.Services.Implementations
{
    public class CostService : ICostService
    {
        private readonly ICostRepository _costRepository;
        private readonly IMapper _mapper;

        public CostService(ICostRepository costRepository, IMapper mapper)
        {
            _costRepository = costRepository;
            _mapper = mapper;
        }

        public async Task<IEnumerable<CostDto>> GetAllAsync()
        {
            var costs = await _costRepository.GetAllAsync();
            return _mapper.Map<IEnumerable<CostDto>>(costs);
        }

        public async Task<CostDto> GetByIdAsync(int id)
        {
            var cost = await _costRepository.GetByIdAsync(id);
            return _mapper.Map<CostDto>(cost);
        }

        public async Task<IEnumerable<CostDto>> GetByCategoryIdAsync(int categoryId)
        {
            var costs = await _costRepository.GetByCategoryIdAsync(categoryId);
            return _mapper.Map<IEnumerable<CostDto>>(costs);
        }

        public async Task<CostDto> CreateAsync(CostDto dto)
        {
            var cost = _mapper.Map<cost_master>(dto);
            // Assuming Mapper handles all fields including CategoryId mapping
            var createdCost = await _costRepository.AddAsync(cost);
            return _mapper.Map<CostDto>(createdCost);
        }

        public async Task<CostDto> UpdateAsync(int id, CostDto dto)
        {
            var existingCost = await _costRepository.GetByIdAsync(id);
            if (existingCost == null) return null;

            _mapper.Map(dto, existingCost);
            await _costRepository.UpdateAsync(existingCost);
            return _mapper.Map<CostDto>(existingCost);
        }

        public async Task<bool> DeleteAsync(int id)
        {
            return await _costRepository.DeleteAsync(id);
        }
    }
}
