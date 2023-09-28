//
//  NetworkHelper.swift
//  Weather
//
//  Created by Artem Mkrtchyan on 9/27/23.
//

import Foundation
import CoreLocation

class NetworkHelper {
    
    private let networkSessionService: NetworkSessionServiceable
    private let decoder: JSONDecoder
    
    
    private static func executeRequest(){
        
    }
    
    init(networkSessionService: NetworkSessionServiceable = URLSession.shared, decoder: JSONDecoder = JSONDecoder()){
        self.networkSessionService = networkSessionService
        self.decoder = decoder
    }
    
    func getWeather(for geodata: GeoCoding , completion: @escaping(Result<WeatherResponse, Error>) -> Void ) {
        let urlRequest = URLRequest(url: URL(string:  URLProvider.Endpoint.weather(geodata).endpointPathString)!)
        
        do {
            
            networkSessionService.dataTask(using: urlRequest) { (result: Result<WeatherResponse, Error>) in
                switch result {
                case .success(let response):
                    completion(.success(response))
                case .failure(let error):
                    completion(.failure(error))
                }
            }
            
        }
    }
    
    
    func getGeoData(for text: String, completion: @escaping(Result<[GeoCoding], Error>) -> Void ) {
        let urlRequest = URLRequest(url: URL(string: URLProvider.Endpoint.geo(text).endpointPathString)!)
        
        networkSessionService.dataTask(using: urlRequest) { (result: Result<[GeoCoding], Error>) in
            switch result {
            case .success(let response):
                completion(.success(response))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }
    
    func getReversed(for coordinate: CLLocationCoordinate2D, completion: @escaping(Result<[GeoCoding], Error>) -> Void ) {
        let urlRequest = URLRequest(url: URL(string: URLProvider.Endpoint.geoReverse(lat: coordinate.latitude, lon: coordinate.longitude).endpointPathString)!)
        
        networkSessionService.dataTask(using: urlRequest) { (result: Result<[GeoCoding], Error>) in
            switch result {
            case .success(let response):
                completion(.success(response))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }
    
}





