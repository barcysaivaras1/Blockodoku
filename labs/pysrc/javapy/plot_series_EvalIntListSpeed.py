import matplotlib
matplotlib.use('Agg')  # Use non-GUI Agg backend
import matplotlib.pyplot as plt
import json
from io import BytesIO
import base64


def plot_and_return_image(json_data):
    # Parse the JSON data
    print(f"Plotting some data!!!")
    data = json.loads(json_data)
    x_values = data['xValues']
    data_series = data['results']



    # Create the plot
    plt.figure(figsize=(10, 6))
    for series_name, y_values in data_series.items():
        plt.plot(x_values,y_values, label=series_name)

    plt.xlabel('Number of Appends')
    plt.ylabel('Time (ms)')
    plt.title('Time for n appends')
    plt.legend()
    plt.grid(True)

    # Save the plot to a buffer as PNG
    buf = BytesIO()
    plt.savefig(buf, format='png')
    buf.seek(0)

    # Encode the image to Base64
    img_base64 = base64.b64encode(buf.read()).decode('utf-8')
    buf.close()

    return img_base64

# add a main function with sample usage


# Main function for standalone testing
def main():
    # Create some sample data
    sample_data = {
        "xValues": [10000, 20000, 30000, 40000, 50000, 60000, 70000, 80000, 90000, 100000],
        "results": {
            "IntArrayList": [10, 20, 30, 40, 50, 60, 70, 80, 90, 100],
            "IntLinkedList": [15, 25, 35, 45, 55, 65, 75, 85, 95, 105]
        }
    }

    # Convert the data to JSON
    json_data = json.dumps(sample_data)

    # Call the plotting function and get the Base64 image
    img_base64 = plot_and_return_image(json_data)

    # For testing purposes, print the Base64 string (trimmed for readability)
    print(f"Base64-encoded image: {img_base64[:100]}...")  # Print only the first 100 characters
    print(len(img_base64))


if __name__ == "__main__":
    main()