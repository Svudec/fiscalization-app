FROM mcr.microsoft.com/dotnet/aspnet:6.0 AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
WORKDIR /src
COPY ["FiscalizationNetCore.WebApi/FiscalizationNetCore.WebApi.csproj", "FiscalizationNetCore.WebApi/"]
RUN dotnet restore "FiscalizationNetCore.WebApi/FiscalizationNetCore.WebApi.csproj"
COPY . .
#ADD FiscalizationNetCore.WebApi/cert.pfx /usr/local/share/ca-certificates/cert.pfx
#RUN chmod 644 /usr/local/share/ca-certificates/cert.pfx && update-ca-certificates
WORKDIR "/src/FiscalizationNetCore.WebApi"
RUN dotnet build "FiscalizationNetCore.WebApi.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "FiscalizationNetCore.WebApi.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "FiscalizationNetCore.WebApi.dll"]
