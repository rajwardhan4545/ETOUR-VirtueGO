
$baseUrl = "http://localhost:5143/api"

# 1. Get a valid Booking ID
Write-Host "Fetching bookings to find a valid BookingId..."
try {
    $bookingsResponse = Invoke-RestMethod -Uri "$baseUrl/Booking" -Method Get
    if ($bookingsResponse.Count -eq 0) {
        Write-Error "No bookings found! Cannot create a passenger without a valid BookingId."
        exit 1
    }
    $bookingId = $bookingsResponse[0].bookingId
    if (-not $bookingId) { $bookingId = $bookingsResponse[0].item.bookingId }
    Write-Host "Using BookingId: $bookingId"
}
catch {
    Write-Error "Failed to fetch bookings. Is the server running on $baseUrl? Error: $_"
    exit 1
}

# 2. Create Passenger Payload
$passenger = @{
    bookingId = $bookingId
    paxName = "Test Passenger Restart"
    paxBirthdate = "1990-01-01T00:00:00"
    paxType = "Adult"
    paxAmount = 150.00
}
$body = $passenger | ConvertTo-Json

# 3. Send POST Request (Singular: api/passenger/add)
Write-Host "Sending POST request to $baseUrl/passenger/add..."
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/passenger/add" -Method Post -Body $body -ContentType "application/json"
    Write-Host "POST Response received:"
    $response | ConvertTo-Json -Depth 2
    $paxId = $response.id
    Write-Host "`nSUCCESS: Passenger created with ID: $paxId"
}
catch {
    Write-Error "POST request failed. Error: $_"
    $_.Exception.Response
    try {
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        $reader.ReadToEnd()
    } catch {}
    exit 1
}

# 4. Send GET Request by ID
if ($paxId) {
    Write-Host "`nSending GET request to $baseUrl/passenger/$paxId..."
    try {
        $getResponse = Invoke-RestMethod -Uri "$baseUrl/passenger/$paxId" -Method Get
        Write-Host "GET Response received:"
        $getResponse | ConvertTo-Json -Depth 2
        Write-Host "`nSUCCESS: Passenger retrieved."
    }
    catch {
        Write-Error "GET request failed. Error: $_"
         $_.Exception.Response
    }
}
