//
//  NetworkSessionServiceable.swift
//  Weather
//
//  Created by Artem Mkrtchyan on 9/27/23.
//

import Foundation

typealias NetworkVoidResponse = Result<Void, Error>
typealias NetworkDataResponse = Result<(Data?, URLResponse?), Error>

protocol NetworkSessionServiceable {
    @discardableResult
    func dataTask(using request: URLRequest, with completion: @escaping (NetworkDataResponse) -> Void) -> URLSessionDataTask
    @discardableResult
    func dataTask<T: Decodable>(using request: URLRequest, with completion: @escaping (Result<T, Error>) -> Void) -> URLSessionDataTask
}

extension URLSession: NetworkSessionServiceable {
    
    @discardableResult
    func dataTask<T: Decodable>(using request: URLRequest, with completion: @escaping (Result<T, Error>) -> Void) -> URLSessionDataTask {
        dataTask(using: request) { [unowned self] (response: NetworkDataResponse) in
            switch response {
            case .success((let data, _)):
                guard let data = data else {
                    print("Data is empty for request: \(request)")
                    completion(.failure(WeatherError.dataEmpty))
                    return
                }
                var decodedString = String(decoding: data, as: UTF8.self)
                decodedString = decodedString.isEmpty ? "\(data)": decodedString
                do {
                    let decodedObject: T = try JSONDecoder().decode(T.self, from: data)
                    completion(.success(decodedObject))
                } catch {
                    completion(.failure(error))
                }
            case .failure(let error):
                print("Request failed: \(error)")
                completion(.failure(error))
            }
        }
    }
    
    @discardableResult
    func dataTask(using request: URLRequest, with completion: @escaping (NetworkDataResponse) -> Void) -> URLSessionDataTask {

        
        let task = dataTask(with: request) { (data, response, error) in
            
            let networkResponse: NetworkDataResponse
            
            if let error = error {
                print("Network request failed: \(String(describing: request.url?.absoluteString)): \n\(error)")
                networkResponse = .failure(error)
            } else if let response = response as? HTTPURLResponse {
                let statusCode = response.statusCode
                if statusCode == 200 {
                    networkResponse = .success((data, response))
                } else {
                    print("Bad status code: \(statusCode)")
                    if let data = data {
                        do {
                            let errorResponse = try JSONDecoder().decode(ErrorResponse.self, from: data)
                            networkResponse = .failure(WeatherError.errorResponse(statusCode: statusCode, error: errorResponse.message!))

                        } catch {
                            print("Failed to decode JSON: \(error)")
                            networkResponse = .failure(WeatherError.badStatusCode(statusCode: statusCode))
                        }
                    } else {
                        networkResponse = .failure(WeatherError.badStatusCode(statusCode: statusCode))
                    }
                }
            } else {
                networkResponse = .failure(WeatherError.unexpectedResponse(response))
            }
            
            completion(networkResponse)
        }
        
        task.resume()
        
        return task
    }
}
