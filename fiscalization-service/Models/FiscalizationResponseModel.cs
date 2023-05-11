namespace FiscalizationNetCore.WebApi
{
    public class FiscalizationResponseModel
    {
        public bool Success { get; set; }
        public string Message { get; set; }
        public string InvoiceId { get; set; }

        public override string ToString()
        {
            return $"{InvoiceId}##razmak##{Success}##razmak##{Message}";
        }
    }
}
