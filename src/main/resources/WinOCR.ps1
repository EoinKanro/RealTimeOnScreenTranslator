# WinOCR.ps1 — Persistent Windows OCR over stdin/stdout
using namespace Windows.Storage
using namespace Windows.Graphics.Imaging

Add-Type -AssemblyName System.Runtime.WindowsRuntime

# Preload WinRT types
$null = [Windows.Media.Ocr.OcrEngine,                Windows.Foundation,      ContentType = WindowsRuntime]
$null = [Windows.Foundation.IAsyncOperation`1,       Windows.Foundation,      ContentType = WindowsRuntime]
$null = [Windows.Graphics.Imaging.SoftwareBitmap,    Windows.Foundation,      ContentType = WindowsRuntime]
$null = [Windows.Storage.Streams.RandomAccessStream, Windows.Storage.Streams, ContentType = WindowsRuntime]

# Create OCR engine once
$ocrEngine = [Windows.Media.Ocr.OcrEngine]::TryCreateFromUserProfileLanguages()

if ($null -eq $ocrEngine) {
    Write-Output "ERROR: Failed to create OcrEngine. Install a language pack."
    exit 1
}

# Await helper
$getAwaiterBaseMethod = [WindowsRuntimeSystemExtensions].GetMember('GetAwaiter').
                            Where({ $PSItem.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' }, 'First')[0]

Function Await {
    param($AsyncTask, $ResultType)
    $getAwaiterBaseMethod.MakeGenericMethod($ResultType).Invoke($null, @($AsyncTask)).GetResult()
}

# Load image from file path
Function Load-Image($path) {
    $path = $ExecutionContext.SessionState.Path.GetUnresolvedProviderPathFromPSPath($path)

    $storageFile = Await ([StorageFile]::GetFileFromPathAsync($path)) ([StorageFile])
    $fileStream  = Await ($storageFile.OpenAsync([FileAccessMode]::Read)) ([Streams.IRandomAccessStream])
    $decoder     = Await ([BitmapDecoder]::CreateAsync($fileStream)) ([BitmapDecoder])
    $softwareBitmap = Await ($decoder.GetSoftwareBitmapAsync()) ([SoftwareBitmap])
    return $softwareBitmap
}

# Main loop
while ($true) {
    $line = [Console]::ReadLine()
    if ($null -eq $line -or $line -eq "EXIT") { break }

    try {
        $softwareBitmap = Load-Image $line
        $ocrResult = Await ($ocrEngine.RecognizeAsync($softwareBitmap)) ([Windows.Media.Ocr.OcrResult])
        Write-Output $ocrResult.Text
        [Console]::Out.Flush()
    }
    catch {
        Write-Output "OCR Error: $_"
        [Console]::Out.Flush()
    }
}